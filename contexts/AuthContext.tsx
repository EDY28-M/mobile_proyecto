import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as WebBrowser from 'expo-web-browser';
import * as Google from 'expo-auth-session/providers/google';
import jwtDecode from 'jwt-decode';
import { makeRedirectUri } from 'expo-auth-session';
import { User } from '../types/api';
import { apiService } from '../services/api';

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  loginWithGoogle: () => Promise<void>;
  logout: () => Promise<void>;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  WebBrowser.maybeCompleteAuthSession();

  const [_googleRequest, googleResponse, promptGoogleLogin] =
    Google.useIdTokenAuthRequest({
      clientId:
        '659951375693-30d2b3d30ug2ccucoi4hr6jbdhte108r.apps.googleusercontent.com',
      issuer: 'https://accounts.google.com',
      scopes: ['openid', 'profile', 'email'],
      strictDiscoveryDocumentValidation: false,
      redirectUri: makeRedirectUri({ useProxy: true }),
    });

  useEffect(() => {
    if (googleResponse?.type === 'success' && googleResponse.params.id_token) {
      (async () => {
        const token = googleResponse.params.id_token as string;
        await AsyncStorage.setItem('google_token', token);
        const info: any = jwtDecode(token);
        setUser({
          id: info.sub ? Number(info.sub) : -1,
          name: info.name,
          email: info.email,
          created_at: '',
          updated_at: '',
        });
      })();
    }
  }, [googleResponse]);

  const isAuthenticated = !!user;

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const authenticated = await apiService.isAuthenticated();
      if (authenticated) {
        const userData = await apiService.getMe();
        setUser(userData);
        return;
      }

      const googleToken = await AsyncStorage.getItem('google_token');
      if (googleToken) {
        const info: any = jwtDecode(googleToken);
        setUser({
          id: info.sub ? Number(info.sub) : -1,
          name: info.name,
          email: info.email,
          created_at: '',
          updated_at: '',
        });
      }
    } catch (error) {
      console.error('Auth check failed:', error);
      await apiService.removeAuthToken();
      await AsyncStorage.removeItem('google_token');
    } finally {
      setIsLoading(false);
    }
  };

  const login = async (email: string, password: string) => {
    try {
      const response = await apiService.login({ email, password });
      await apiService.saveAuthToken(response.access_token);
      setUser(response.user);
    } catch (error) {
      throw error;
    }
  };

  const register = async (name: string, email: string, password: string) => {
    try {
      const response = await apiService.register({ name, email, password });
      await apiService.saveAuthToken(response.access_token);
      setUser(response.user);
    } catch (error) {
      throw error;
    }
  };

  const loginWithGoogle = async () => {
    await promptGoogleLogin();
  };

  const logout = async () => {
    try {
      await apiService.removeAuthToken();
      await AsyncStorage.removeItem('google_token');
      setUser(null);
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

  const refreshUser = async () => {
    try {
      const token = await AsyncStorage.getItem('auth_token');
      if (token) {
        const userData = await apiService.getMe();
        setUser(userData);
        return;
      }

      const googleToken = await AsyncStorage.getItem('google_token');
      if (googleToken) {
        const info: any = jwtDecode(googleToken);
        setUser({
          id: info.sub ? Number(info.sub) : -1,
          name: info.name,
          email: info.email,
          created_at: '',
          updated_at: '',
        });
      }
    } catch (error) {
      console.error('Refresh user failed:', error);
      await logout();
    }
  };

  const value: AuthContextType = {
    user,
    isLoading,
    isAuthenticated,
    login,
    register,
    loginWithGoogle,
    logout,
    refreshUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}