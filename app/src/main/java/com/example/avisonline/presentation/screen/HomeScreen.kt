package com.example.avisonline.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.avisonline.data.model.Category
import com.example.avisonline.data.model.Product
import com.example.avisonline.presentation.viewmodel.HomeState
import com.example.avisonline.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        homeViewModel.fetchHomeData()
    }

    val state by homeViewModel.homeState.collectAsState()

    when (val homeState = state) {
        is HomeState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = homeState.message)
            }
        }
        is HomeState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    SectionTitle(title = "Productos Nuevos")
                    NewProductsList(products = homeState.data.newProducts.data)
                }
                item {
                    SectionTitle(title = "Categorías")
                    CategoriesList(categories = homeState.data.categories)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun NewProductsList(products: List<Product>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductCard(product)
        }
    }
}

@Composable
fun CategoriesList(categories: List<Category>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { category ->
            CategoryChip(category)
        }
    }
}


@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imagen,
                contentDescription = product.title,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.resumen,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "S/ ${product.price}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            AsyncImage(
                model = category.imagen,
                contentDescription = category.name,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = category.name, fontWeight = FontWeight.SemiBold)
        }
    }
}