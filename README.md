# Tienda de Libos

Sistema que permitira la gestion y venta de libros de una libreria

## Modelo Entidad–Relación (MER)
```text
Cliente (1)───(N) Venta (1)───(N) DetalleVenta (N)───(1) Libro (N)───(N) Autor
                                                   │
                                                   └──(1) GeneroLiterario
```
													   
| Entidad              | Descripción                                                                     |
| -------------------- | ------------------------------------------------------------------------------- |
| **Libro**            | Contiene información de los libros disponibles (precio, stock, género, moneda). |
| **Género Literario** | Catálogo de categorías o géneros.                                               |
| **Autor**            | Información de los autores.                                                     |
| **Cliente**          | Datos de los compradores.                                                       |
| **Venta**            | Registro principal de cada venta (fecha, cliente, total, moneda).               |
| **DetalleVenta**     | Relación entre ventas y libros vendidos, con precios e importes.                |
| **LibroAutor**       | Relación N:M entre libros y autores.                                            |

## Test
```text
src/test/java/gm/tienda_libros/
├── validationtest/ 🧠 Unitario
│    └── ClienteValidacionTest.java
│	 └── VentaValidacionTest.java 
│
├── repositorytest/ 🧱 Integración
│    └── ClienteRepositoryTest.java
│    └── VentaRepositoryTest.java    
│
├── servicetest/ 🧠 Unitario
│    └── ClienteServiceTest.java
│    └── VentaServiceTest.java      
│
└── controllertest/ 🧱 Integración
     └── ClienteControllerTest.java
     └── VentaControllerTest.java
```

