import React, { useState, useEffect, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, RefreshControl, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import { Product } from '../../types/api';
import { apiService } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import { ProductCard } from '../../components/ProductCard';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { ErrorMessage } from '../../components/ErrorMessage';
import { CustomButton } from '../../components/CustomButton';
import { Plus } from 'lucide-react-native';

export default function MyProductsScreen() {
  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState<number | null>(null);

  const { user } = useAuth();
  const router = useRouter();

  const loadMyProducts = useCallback(async (refresh: boolean = false) => {
    try {
      if (refresh) {
        setIsRefreshing(true);
      } else {
        setIsLoading(true);
      }
      setError(null);

      const response = await apiService.getProducts(1, {
        user_id: user?.id,
      });
      
      setProducts(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to load your products');
    } finally {
      setIsLoading(false);
      setIsRefreshing(false);
    }
  }, [user?.id]);

  useEffect(() => {
    loadMyProducts();
  }, [loadMyProducts]);

  const handleRefresh = () => {
    loadMyProducts(true);
  };

  const handleDelete = async (productId: number) => {
    Alert.alert(
      'Delete Product',
      'Are you sure you want to delete this product? This action cannot be undone.',
      [
        {
          text: 'Cancel',
          style: 'cancel',
        },
        {
          text: 'Delete',
          style: 'destructive',
          onPress: async () => {
            try {
              setIsDeleting(productId);
              await apiService.deleteProduct(productId);
              setProducts(prev => prev.filter(p => p.id !== productId));
            } catch (err: any) {
              Alert.alert('Error', err.message || 'Failed to delete product');
            } finally {
              setIsDeleting(null);
            }
          },
        },
      ]
    );
  };

  const handleCreateProduct = () => {
    if (products.length >= 3) {
      Alert.alert(
        'Limit Reached',
        'You have reached the limit of 3 products. Upgrade your plan to create more products.',
        [{ text: 'OK' }]
      );
      return;
    }
    router.push('/(tabs)/create');
  };

  const renderProduct = ({ item }: { item: Product }) => (
    <ProductCard
      product={item}
      onDelete={handleDelete}
      showDeleteButton={isDeleting !== item.id}
    />
  );

  const renderEmptyState = () => (
    <View style={styles.emptyState}>
      <Text style={styles.emptyTitle}>No Products Yet</Text>
      <Text style={styles.emptySubtitle}>
        Create your first product listing to get started
      </Text>
      <CustomButton
        title="Create Product"
        onPress={handleCreateProduct}
        style={styles.emptyButton}
      />
    </View>
  );

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <ErrorMessage
        message={error}
        onRetry={() => loadMyProducts()}
      />
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <View style={styles.headerContent}>
          <View>
            <Text style={styles.title}>My Products</Text>
            <Text style={styles.subtitle}>
              {products.length}/3 products created
            </Text>
          </View>
          
          <CustomButton
            title="Create"
            onPress={handleCreateProduct}
            size="small"
            disabled={products.length >= 3}
            style={[
              styles.createButton,
              products.length >= 3 && styles.createButtonDisabled,
            ]}
          />
        </View>
        
        {products.length >= 3 && (
          <View style={styles.limitWarning}>
            <Text style={styles.limitText}>
              You've reached the 3-product limit. Upgrade your plan to create more.
            </Text>
          </View>
        )}
      </View>

      <FlatList
        data={products}
        renderItem={renderProduct}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={[
          styles.listContainer,
          products.length === 0 && styles.emptyContainer,
        ]}
        refreshControl={
          <RefreshControl
            refreshing={isRefreshing}
            onRefresh={handleRefresh}
            tintColor="#007AFF"
          />
        }
        ListEmptyComponent={renderEmptyState}
        showsVerticalScrollIndicator={false}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  header: {
    paddingHorizontal: 24,
    paddingVertical: 20,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  headerContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title: {
    fontSize: 28,
    fontWeight: '700',
    color: '#1a1a1a',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 16,
    color: '#666',
  },
  createButton: {
    paddingHorizontal: 16,
  },
  createButtonDisabled: {
    opacity: 0.5,
  },
  limitWarning: {
    marginTop: 12,
    padding: 12,
    backgroundColor: '#FFF3CD',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#FFEAA7',
  },
  limitText: {
    fontSize: 14,
    color: '#856404',
    textAlign: 'center',
  },
  listContainer: {
    padding: 16,
  },
  emptyContainer: {
    flex: 1,
  },
  emptyState: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 40,
  },
  emptyTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: '#1a1a1a',
    marginBottom: 8,
    textAlign: 'center',
  },
  emptySubtitle: {
    fontSize: 16,
    color: '#666',
    textAlign: 'center',
    lineHeight: 24,
    marginBottom: 32,
  },
  emptyButton: {
    paddingHorizontal: 32,
  },
});