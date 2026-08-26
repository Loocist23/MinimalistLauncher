package com.devaz.minimallauncher.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.concurrent.ConcurrentHashMap

/**
 * Cache pour les icônes d'applications converties en ImageBitmap.
 * Évite de recréer les bitmaps à chaque recomposition.
 * 
 * @param maxSize Nombre maximum d'icônes à garder en cache (défaut: 200)
 */
class AppIconCache(private val maxSize: Int = 200) {
    
    private val cache = object : LinkedHashMap<String, ImageBitmap>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, ImageBitmap>): Boolean {
            return size > maxSize
        }
    }
    
    private val pendingRequests = ConcurrentHashMap<String, Boolean>()
    
    /**
     * Obtient l'ImageBitmap pour un drawable donné.
     * Si le drawable est null, retourne null.
     * 
     * @param key Clé unique pour identifier l'icône (ex: packageName)
     * @param drawable Drawable à convertir
     * @return ImageBitmap ou null si la conversion échoue
     */
    fun get(key: String, drawable: Drawable?): ImageBitmap? {
        if (drawable == null) return null
        
        // Vérifier le cache d'abord
        cache[key]?.let { return it }
        
        // Éviter les requêtes dupliquées
        if (pendingRequests.putIfAbsent(key, true) != null) {
            // Une autre requête est déjà en cours pour cette clé
            return null
        }
        
        return try {
            val bitmap = convertDrawableToBitmap(drawable, 48)
            val imageBitmap = bitmap?.asImageBitmap()
            
            if (imageBitmap != null) {
                cache[key] = imageBitmap
            }
            
            pendingRequests.remove(key)
            imageBitmap
            
        } catch (e: Exception) {
            pendingRequests.remove(key)
            null
        }
    }
    
    /**
     * Convertit un Drawable en Bitmap avec une taille maximale.
     * 
     * @param drawable Drawable à convertir
     * @param maxSize Taille maximale en dp (sera convertie en pixels)
     * @return Bitmap ou null si la conversion échoue
     */
    private fun convertDrawableToBitmap(drawable: Drawable, maxSizeDp: Int): Bitmap? {
        return try {
            // Essayer d'abord comme BitmapDrawable (cas le plus courant)
            (drawable as? android.graphics.drawable.BitmapDrawable)?.let { bitmapDrawable ->
                val originalBitmap = bitmapDrawable.bitmap
                // Redimensionner si nécessaire
                if (originalBitmap.width > maxSizeDp || originalBitmap.height > maxSizeDp) {
                    Bitmap.createScaledBitmap(
                        originalBitmap,
                        maxSizeDp,
                        maxSizeDp,
                        true
                    )
                } else {
                    originalBitmap
                }
            } ?: run {
                // Cas général : créer un bitmap et dessiner le drawable
                val width = drawable.intrinsicWidth.coerceAtMost(maxSizeDp)
                val height = drawable.intrinsicHeight.coerceAtMost(maxSizeDp)
                
                val bitmap = Bitmap.createBitmap(
                    width.coerceAtLeast(48),
                    height.coerceAtLeast(48),
                    Bitmap.Config.ARGB_8888
                )
                
                val canvas = android.graphics.Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                
                bitmap
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Nettoie une entrée spécifique du cache.
     */
    fun remove(key: String) {
        cache.remove(key)
    }
    
    /**
     * Nettoie tout le cache.
     */
    fun clear() {
        cache.clear()
        pendingRequests.clear()
    }
    
    /**
     * Taille actuelle du cache.
     */
    fun size(): Int = cache.size
}

// Instance singleton globale pour tout l'application
val appIconCache = AppIconCache(maxSize = 200)
