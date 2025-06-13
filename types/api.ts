export interface User {
  id: number;
  name: string;
  email: string;
  email_verified_at?: string;
  created_at: string;
  updated_at: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  user: User;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface Category {
  id: number;
  name: string;
  slug: string;
  created_at: string;
  updated_at: string;
}

export interface Brand {
  id: number;
  name: string;
  slug: string;
  created_at: string;
  updated_at: string;
}

export interface ConfigResponse {
  categories_first: Category[];
  categories_seconds: Category[];
  categories_thirds: Category[];
  brands: Brand[];
}

export interface Product {
  id: number;
  title: string;
  sku: string;
  resumen: string;
  description?: string;
  price_usd: number;
  price_pen: number;
  portada?: string;
  brand_id: number;
  categorie_first_id: number;
  categorie_second_id?: number;
  categorie_third_id?: number;
  user_id: number;
  tags?: string;
  created_at: string;
  updated_at: string;
  brand?: Brand;
  categorie_first?: Category;
  categorie_second?: Category;
  categorie_third?: Category;
  user?: User;
}

export interface ProductsResponse {
  data: Product[];
  current_page: number;
  last_page: number;
  per_page: number;
  total: number;
}

export interface CreateProductRequest {
  title: string;
  sku: string;
  resumen: string;
  description?: string;
  price_usd: number;
  price_pen: number;
  brand_id: number;
  categorie_first_id: number;
  categorie_second_id?: number;
  categorie_third_id?: number;
  tags?: string;
  portada?: File | string;
}

export interface ApiError {
  message: string;
  errors?: Record<string, string[]>;
}