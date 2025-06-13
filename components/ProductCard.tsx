import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity, Image } from 'react-native';
import { Product } from '../types/api';
import { Trash2, Tag } from 'lucide-react-native';

interface ProductCardProps {
  product: Product;
  onDelete?: (id: number) => void;
  showDeleteButton?: boolean;
}

export function ProductCard({ product, onDelete, showDeleteButton = false }: ProductCardProps) {
  const imageUri = product.portada 
    ? product.portada.startsWith('http') 
      ? product.portada 
      : `https://images.pexels.com/photos/1279330/pexels-photo-1279330.jpeg?auto=compress&cs=tinysrgb&w=400`
    : 'https://images.pexels.com/photos/1279330/pexels-photo-1279330.jpeg?auto=compress&cs=tinysrgb&w=400';

  return (
    <View style={styles.card}>
      <Image source={{ uri: imageUri }} style={styles.image} />
      
      <View style={styles.content}>
        <View style={styles.header}>
          <Text style={styles.title} numberOfLines={2}>
            {product.title}
          </Text>
          {showDeleteButton && onDelete && (
            <TouchableOpacity
              style={styles.deleteButton}
              onPress={() => onDelete(product.id)}
            >
              <Trash2 size={18} color="#FF3B30" />
            </TouchableOpacity>
          )}
        </View>
        
        <Text style={styles.summary} numberOfLines={2}>
          {product.resumen}
        </Text>
        
        <View style={styles.priceContainer}>
          <Text style={styles.priceUsd}>${product.price_usd}</Text>
          <Text style={styles.pricePen}>S/ {product.price_pen}</Text>
        </View>
        
        <View style={styles.footer}>
          {product.brand && (
            <View style={styles.brandContainer}>
              <Tag size={12} color="#666" />
              <Text style={styles.brandText}>{product.brand.name}</Text>
            </View>
          )}
          
          {product.categorie_first && (
            <Text style={styles.categoryText}>
              {product.categorie_first.name}
            </Text>
          )}
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    marginBottom: 16,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  image: {
    width: '100%',
    height: 200,
    borderTopLeftRadius: 12,
    borderTopRightRadius: 12,
    backgroundColor: '#f5f5f5',
  },
  content: {
    padding: 16,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 8,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: '#1a1a1a',
    flex: 1,
    marginRight: 8,
  },
  deleteButton: {
    padding: 4,
  },
  summary: {
    fontSize: 14,
    color: '#666',
    lineHeight: 20,
    marginBottom: 12,
  },
  priceContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
  },
  priceUsd: {
    fontSize: 20,
    fontWeight: '700',
    color: '#007AFF',
    marginRight: 12,
  },
  pricePen: {
    fontSize: 16,
    fontWeight: '600',
    color: '#34C759',
  },
  footer: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  brandContainer: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  brandText: {
    fontSize: 12,
    color: '#666',
    marginLeft: 4,
  },
  categoryText: {
    fontSize: 12,
    color: '#007AFF',
    fontWeight: '500',
  },
});