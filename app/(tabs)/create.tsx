import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, Alert, Image, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useRouter } from 'expo-router';
import * as ImagePicker from 'expo-image-picker';
import { CustomInput } from '../../components/CustomInput';
import { CustomButton } from '../../components/CustomButton';
import { Picker } from '../../components/Picker';
import { LoadingSpinner } from '../../components/LoadingSpinner';
import { useApp } from '../../contexts/AppContext';
import { useAuth } from '../../contexts/AuthContext';
import { apiService } from '../../services/api';
import { Camera, X } from 'lucide-react-native';

interface FormData {
  title: string;
  sku: string;
  resumen: string;
  description: string;
  price_usd: string;
  price_pen: string;
  brand_id: string;
  categorie_first_id: string;
  categorie_second_id: string;
  categorie_third_id: string;
  tags: string;
}

export default function CreateProductScreen() {
  const [formData, setFormData] = useState<FormData>({
    title: '',
    sku: '',
    resumen: '',
    description: '',
    price_usd: '',
    price_pen: '',
    brand_id: '',
    categorie_first_id: '',
    categorie_second_id: '',
    categorie_third_id: '',
    tags: '',
  });
  
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [myProductsCount, setMyProductsCount] = useState(0);

  const { config, isLoadingConfig } = useApp();
  const { user } = useAuth();
  const router = useRouter();

  useEffect(() => {
    checkProductsCount();
  }, []);

  const checkProductsCount = async () => {
    try {
      const response = await apiService.getProducts(1, { user_id: user?.id });
      setMyProductsCount(response.data.length);
    } catch (error) {
      console.error('Failed to check products count:', error);
    }
  };

  const updateFormData = (field: keyof FormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  const pickImage = async () => {
    const { status } = await ImagePicker.requestMediaLibraryPermissionsAsync();
    
    if (status !== 'granted') {
      Alert.alert('Permission Required', 'Sorry, we need camera roll permissions to select images.');
      return;
    }

    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 0.8,
    });

    if (!result.canceled) {
      setSelectedImage(result.assets[0].uri);
    }
  };

  const removeImage = () => {
    setSelectedImage(null);
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.title.trim()) newErrors.title = 'Title is required';
    if (!formData.sku.trim()) newErrors.sku = 'SKU is required';
    if (!formData.resumen.trim()) newErrors.resumen = 'Summary is required';
    if (!formData.price_usd.trim()) newErrors.price_usd = 'USD price is required';
    if (!formData.price_pen.trim()) newErrors.price_pen = 'PEN price is required';
    if (!formData.brand_id) newErrors.brand_id = 'Brand is required';
    if (!formData.categorie_first_id) newErrors.categorie_first_id = 'Primary category is required';

    // Validate numeric fields
    if (formData.price_usd && isNaN(Number(formData.price_usd))) {
      newErrors.price_usd = 'USD price must be a valid number';
    }
    if (formData.price_pen && isNaN(Number(formData.price_pen))) {
      newErrors.price_pen = 'PEN price must be a valid number';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async () => {
    if (myProductsCount >= 3) {
      Alert.alert(
        'Limit Reached',
        'You have reached the limit of 3 products. Upgrade your plan to create more products.'
      );
      return;
    }

    if (!validateForm()) return;

    try {
      setIsLoading(true);

      const productData = {
        title: formData.title.trim(),
        sku: formData.sku.trim(),
        resumen: formData.resumen.trim(),
        description: formData.description.trim() || undefined,
        price_usd: Number(formData.price_usd),
        price_pen: Number(formData.price_pen),
        brand_id: Number(formData.brand_id),
        categorie_first_id: Number(formData.categorie_first_id),
        categorie_second_id: formData.categorie_second_id ? Number(formData.categorie_second_id) : undefined,
        categorie_third_id: formData.categorie_third_id ? Number(formData.categorie_third_id) : undefined,
        tags: formData.tags.trim() || undefined,
        portada: selectedImage || undefined,
      };

      await apiService.createProduct(productData);
      
      Alert.alert(
        'Success',
        'Product created successfully!',
        [
          {
            text: 'OK',
            onPress: () => router.push('/(tabs)/my-products'),
          },
        ]
      );
    } catch (error: any) {
      Alert.alert('Error', error.message || 'Failed to create product');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoadingConfig) {
    return <LoadingSpinner />;
  }

  if (!config) {
    return (
      <SafeAreaView style={styles.container}>
        <Text style={styles.errorText}>Failed to load configuration</Text>
      </SafeAreaView>
    );
  }

  const brandOptions = config.brands.map(brand => ({
    label: brand.name,
    value: brand.id,
  }));

  const categoryFirstOptions = config.categories_first.map(cat => ({
    label: cat.name,
    value: cat.id,
  }));

  const categorySecondOptions = config.categories_seconds.map(cat => ({
    label: cat.name,
    value: cat.id,
  }));

  const categoryThirdOptions = config.categories_thirds.map(cat => ({
    label: cat.name,
    value: cat.id,
  }));

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView style={styles.scrollView} showsVerticalScrollIndicator={false}>
        <View style={styles.header}>
          <Text style={styles.title}>Create Product</Text>
          <Text style={styles.subtitle}>
            {myProductsCount}/3 products created
          </Text>
        </View>

        <View style={styles.form}>
          {/* Image Picker */}
          <View style={styles.imageSection}>
            <Text style={styles.label}>Product Image</Text>
            {selectedImage ? (
              <View style={styles.imageContainer}>
                <Image source={{ uri: selectedImage }} style={styles.selectedImage} />
                <TouchableOpacity style={styles.removeImageButton} onPress={removeImage}>
                  <X size={20} color="#fff" />
                </TouchableOpacity>
              </View>
            ) : (
              <TouchableOpacity style={styles.imagePicker} onPress={pickImage}>
                <Camera size={32} color="#666" />
                <Text style={styles.imagePickerText}>Select Image</Text>
              </TouchableOpacity>
            )}
          </View>

          <CustomInput
            label="Title *"
            placeholder="Enter product title"
            value={formData.title}
            onChangeText={(value) => updateFormData('title', value)}
            error={errors.title}
          />

          <CustomInput
            label="SKU *"
            placeholder="Enter product SKU"
            value={formData.sku}
            onChangeText={(value) => updateFormData('sku', value)}
            error={errors.sku}
          />

          <CustomInput
            label="Summary *"
            placeholder="Brief product summary"
            value={formData.resumen}
            onChangeText={(value) => updateFormData('resumen', value)}
            multiline
            numberOfLines={3}
            error={errors.resumen}
          />

          <CustomInput
            label="Description"
            placeholder="Detailed product description (optional)"
            value={formData.description}
            onChangeText={(value) => updateFormData('description', value)}
            multiline
            numberOfLines={4}
          />

          <View style={styles.priceRow}>
            <View style={styles.priceField}>
              <CustomInput
                label="Price USD *"
                placeholder="0.00"
                value={formData.price_usd}
                onChangeText={(value) => updateFormData('price_usd', value)}
                keyboardType="numeric"
                error={errors.price_usd}
              />
            </View>
            <View style={styles.priceField}>
              <CustomInput
                label="Price PEN *"
                placeholder="0.00"
                value={formData.price_pen}
                onChangeText={(value) => updateFormData('price_pen', value)}
                keyboardType="numeric"
                error={errors.price_pen}
              />
            </View>
          </View>

          <Picker
            label="Brand *"
            placeholder="Select a brand"
            options={brandOptions}
            value={formData.brand_id}
            onValueChange={(value) => updateFormData('brand_id', value.toString())}
            error={errors.brand_id}
          />

          <Picker
            label="Primary Category *"
            placeholder="Select primary category"
            options={categoryFirstOptions}
            value={formData.categorie_first_id}
            onValueChange={(value) => updateFormData('categorie_first_id', value.toString())}
            error={errors.categorie_first_id}
          />

          <Picker
            label="Secondary Category"
            placeholder="Select secondary category (optional)"
            options={categorySecondOptions}
            value={formData.categorie_second_id}
            onValueChange={(value) => updateFormData('categorie_second_id', value.toString())}
          />

          <Picker
            label="Third Category"
            placeholder="Select third category (optional)"
            options={categoryThirdOptions}
            value={formData.categorie_third_id}
            onValueChange={(value) => updateFormData('categorie_third_id', value.toString())}
          />

          <CustomInput
            label="Tags"
            placeholder="Enter tags (comma separated)"
            value={formData.tags}
            onChangeText={(value) => updateFormData('tags', value)}
          />

          <CustomButton
            title="Create Product"
            onPress={handleSubmit}
            loading={isLoading}
            disabled={myProductsCount >= 3}
            style={styles.submitButton}
          />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f8f9fa',
  },
  scrollView: {
    flex: 1,
  },
  header: {
    paddingHorizontal: 24,
    paddingVertical: 20,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
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
  form: {
    padding: 24,
  },
  imageSection: {
    marginBottom: 16,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1a1a1a',
    marginBottom: 8,
  },
  imageContainer: {
    position: 'relative',
  },
  selectedImage: {
    width: '100%',
    height: 200,
    borderRadius: 8,
    backgroundColor: '#f5f5f5',
  },
  removeImageButton: {
    position: 'absolute',
    top: 8,
    right: 8,
    backgroundColor: 'rgba(0, 0, 0, 0.6)',
    borderRadius: 16,
    width: 32,
    height: 32,
    justifyContent: 'center',
    alignItems: 'center',
  },
  imagePicker: {
    borderWidth: 2,
    borderColor: '#ddd',
    borderStyle: 'dashed',
    borderRadius: 8,
    height: 120,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f9f9f9',
  },
  imagePickerText: {
    fontSize: 14,
    color: '#666',
    marginTop: 8,
  },
  priceRow: {
    flexDirection: 'row',
    gap: 16,
  },
  priceField: {
    flex: 1,
  },
  submitButton: {
    marginTop: 24,
  },
  errorText: {
    fontSize: 16,
    color: '#FF3B30',
    textAlign: 'center',
    margin: 20,
  },
});