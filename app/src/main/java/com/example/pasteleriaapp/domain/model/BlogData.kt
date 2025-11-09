package com.example.pasteleriaapp.domain.model

import androidx.annotation.DrawableRes
import com.example.pasteleriaapp.R

data class BlogPost(
    val id: String, // Identificador único para la navegación
    val titulo: String,
    val autor: String,
    @DrawableRes val imagenRes: Int, // Referencia directa a R.drawable
    val contenido: String
)

object BlogData {
    // Aquí hardcodeamos nuestros artículos
    val posts = listOf(
        BlogPost(
            id = "receta-cheesecake",
            titulo = "Receta: Cheesecake de Frutos Rojos (Sin Azúcar)",
            autor = "Por Estudiantes de Gastronomía Duoc UC",
            imagenRes = R.drawable.cheesecake_sin_azucar_blog, // Imagen que mencionaste
            contenido = """
¡Aprende a preparar un delicioso Cheesecake sin azúcar, perfecto para disfrutar sin culpas! Esta receta es aportada por nuestros talentosos estudiantes de gastronomía.

**Ingredientes para la base:**
- 150g de galletas de avena sin azúcar
- 70g de mantequilla sin sal, derretida

**Ingredientes para el relleno:**
- 500g de queso crema tipo Philadelphia (light)
- 200ml de crema de leche (nata) para montar
- 100g de tu endulzante en polvo preferido (ej. alulosa, stevia)
- 1 cucharadita de extracto de vainilla
- Ralladura de 1 limón
- 3 huevos grandes

**Preparación:**
1.  **Base:** Tritura las galletas hasta que sean un polvo fino. Mézclalas con la mantequilla derretida. Presiona esta mezcla firmemente en la base de un molde desmontable de 20cm. Refrigera mientras preparas el relleno.
2.  **Relleno:** Precalienta el horno a 160°C.
3.  En un bol grande, bate el queso crema con el endulzante hasta que esté suave y sin grumos.
4.  Añade la crema de leche, la vainilla y la ralladura de limón. Mezcla bien.
5.  Incorpora los huevos uno a uno, batiendo lo justo después de cada adición. No batas en exceso para no incorporar aire.
6.  Vierte el relleno sobre la base de galleta refrigerada.
7.  **Horneado:** Hornea durante 50-60 minutos, o hasta que los bordes estén firmes pero el centro aún se vea ligeramente tembloroso.
8.  Apaga el horno y deja enfriar el cheesecake dentro con la puerta entreabierta durante 1 hora.
9.  Refrigera por al menos 4 horas (idealmente toda la noche) antes de desmoldar y servir.

¡Cúbrelo con una mermelada de frutos rojos sin azúcar antes de servir!
            """.trimIndent()
        ),
        BlogPost(
            id = "consejos-reposteria",
            titulo = "5 Consejos Clave para una Torta Perfecta",
            autor = "Por Estudiantes de Gastronomía Duoc UC",
            imagenRes = R.drawable.torta_perfecta, // Imagen que mencionaste
            contenido = """
Lograr esa torta esponjosa y deliciosa de pastelería requiere técnica. Aquí te dejamos 5 consejos esenciales de nuestros estudiantes de gastronomía:

**1. Ingredientes a Temperatura Ambiente:**
Sacar los huevos, la mantequilla y la leche de la refrigeradora al menos 30 minutos antes de empezar es crucial. Los ingredientes a temperatura ambiente se emulsionan mucho mejor, creando una masa más homogénea y un bizcocho más esponjoso.

**2. Pesar los Ingredientes (Precisión):**
La repostería es una ciencia exacta. A diferencia de la cocina salada, "un poquito más" de harina o "un poquito menos" de azúcar puede arruinar la textura. Usa una pesa de cocina (gramera) en lugar de tazas medidoras para obtener resultados consistentes.

**3. No Batir en Exceso la Harina:**
Una vez que añades la harina a la mezcla húmeda, estás desarrollando el gluten. Si bates demasiado, el gluten se fortalece y el resultado será una torta densa y "apretada" en lugar de suave. Mezcla solo hasta que los ingredientes secos estén integrados.

**4. Conocer tu Horno:**
Cada horno es un mundo. Algunos calientan más por atrás, otros por abajo. Usa un termómetro de horno para asegurarte de que la temperatura real coincide con la que seleccionaste. Y, ¡nunca abras la puerta del horno durante los primeros 20 minutos! El cambio brusco de temperatura hará que tu bizcocho se hunda en el centro.

**5. El Enfriado es Parte del Proceso:**
La paciencia es un ingrediente más. Dejar enfriar la torta en el molde sobre una rejilla durante 10-15 minutos antes de desmoldarla es vital. Si intentas desmoldarla caliente, es muy probable que se rompa.
            """.trimIndent()
        )
    )

    /**
     * Busca un post por su ID.
     */
    fun getPostById(id: String): BlogPost? {
        return posts.find { it.id == id }
    }
}