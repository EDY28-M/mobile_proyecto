import AsyncStorage from '@react-native-async-storage/async-storage';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  ConfigResponse,
  ProductsResponse,
  CreateProductRequest,
  Product,
  User,
  ApiError,
} from '../types/api';

const API_BASE_URL = process.env.EXPO_PUBLIC_API_URL || 'http://127.0.0.1:8000/api';

class ApiService {
  private async getAuthToken(): Promise<string | null> {
    try {
      return await AsyncStorage.getItem('auth_token');
    } catch (error) {
      console.error('Error getting auth token:', error);
      return null;
    }
  }

private async makeRequest<T>(
  endpoint: string,
  options: RequestInit = {}
): Promise<T> {
  const token = await this.getAuthToken();

  // Crea un objeto Headers y úsalos “type-safe”
  const headers = new Headers({
    'Content-Type': 'application/json',
    'Accept':       'application/json',
    // Desenpaqueta aquí si tu RequestInit trae headers
    ...(options.headers as Record<string,string>),
  });

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,  // pasas la instancia de Headers
  });

    // resto igual…



    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  private async makeFormDataRequest<T>(
    endpoint: string,
    formData: FormData
  ): Promise<T> {
    const token = await this.getAuthToken();
    
    const headers: HeadersInit = {
      'Accept': 'application/json',
    };

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: 'POST',
      headers,
      body: formData,
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  // Auth endpoints
  async register(data: RegisterRequest): Promise<AuthResponse> {
    return this.makeRequest<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    return this.makeRequest<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getMe(): Promise<User> {
    return this.makeRequest<User>('/auth/me');
  }

  // Config endpoint
  async getConfig(): Promise<ConfigResponse> {
    return this.makeRequest<ConfigResponse>('/admin/config');
  }

  // Products endpoints
  async getProducts(page: number = 1, filters?: Record<string, any>): Promise<ProductsResponse> {
    const params = new URLSearchParams({
      page: page.toString(),
      ...filters,
    });
    
    return this.makeRequest<ProductsResponse>(`/admin/products/index?${params}`);
  }

  async createProduct(data: CreateProductRequest): Promise<Product> {
    const formData = new FormData();
    
    formData.append('title', data.title);
    formData.append('sku', data.sku);
    formData.append('resumen', data.resumen);
    formData.append('description', data.description || '');
    formData.append('price_usd', data.price_usd.toString());
    formData.append('price_pen', data.price_pen.toString());
    formData.append('brand_id', data.brand_id.toString());
    formData.append('categorie_first_id', data.categorie_first_id.toString());
    
    if (data.categorie_second_id) {
      formData.append('categorie_second_id', data.categorie_second_id.toString());
    }
    
    if (data.categorie_third_id) {
      formData.append('categorie_third_id', data.categorie_third_id.toString());
    }
    
    if (data.tags) {
      formData.append('tags', data.tags);
    }
    
    if (data.portada) {
      formData.append('portada', data.portada as any);
    }

    return this.makeFormDataRequest<Product>('/admin/products', formData);
  }

  async deleteProduct(id: number): Promise<void> {
    await this.makeRequest(`/admin/products/${id}`, {
      method: 'DELETE',
    });
  }

  // Auth helpers
  async saveAuthToken(token: string): Promise<void> {
    try {
      await AsyncStorage.setItem('auth_token', token);
    } catch (error) {
      console.error('Error saving auth token:', error);
    }
  }

  async removeAuthToken(): Promise<void> {
    try {
      await AsyncStorage.removeItem('auth_token');
    } catch (error) {
      console.error('Error removing auth token:', error);
    }
  }

  async isAuthenticated(): Promise<boolean> {
    const token = await this.getAuthToken();
    return !!token;
  }
}

export const apiService = new ApiService();