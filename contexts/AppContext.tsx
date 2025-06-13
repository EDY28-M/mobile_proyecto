import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { ConfigResponse } from '../types/api';
import { apiService } from '../services/api';
import { useAuth } from './AuthContext';

interface AppContextType {
  config: ConfigResponse | null;
  isLoadingConfig: boolean;
  refreshConfig: () => Promise<void>;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function useApp() {
  const context = useContext(AppContext);
  if (context === undefined) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}

interface AppProviderProps {
  children: ReactNode;
}

export function AppProvider({ children }: AppProviderProps) {
  const [config, setConfig] = useState<ConfigResponse | null>(null);
  const [isLoadingConfig, setIsLoadingConfig] = useState(false);
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated) {
      loadConfig();
    }
  }, [isAuthenticated]);

  const loadConfig = async () => {
    try {
      setIsLoadingConfig(true);
      const configData = await apiService.getConfig();
      setConfig(configData);
    } catch (error) {
      console.error('Failed to load config:', error);
    } finally {
      setIsLoadingConfig(false);
    }
  };

  const refreshConfig = async () => {
    await loadConfig();
  };

  const value: AppContextType = {
    config,
    isLoadingConfig,
    refreshConfig,
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
}