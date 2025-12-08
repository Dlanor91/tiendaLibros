# Tienda de Libros

Sistema que permitira la gestion y venta de libros en una libreria

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
├── validation/ 🧠 Unitario
│   └── ClienteValidacionTest.java
│	└── VentaValidacionTest.java
│	└── GeneroLiterarioValidacionTest.java
│	└── AutorValidacionTest.java
│	└── LibroValidacionTest.java
│	└── DetalleVentasValidacionTest.java
│
├── repository/ 🧱 Integración
│   └── ClienteRepositoryTest.java
│   └── VentaRepositoryTest.java
│	└── GeneroLiterarioRepositoryTest.java
│	└── AutorRepositoryTest.java
│	└── LibroRepositoryTest.java
│
├── service/ 🧠 Unitario
│   └── ClienteServiceTest.java
│   └── VentaServiceTest.java
│   └── GeneroLiterarioServiceTest.java
│   └── AutorServiceTest.java
│   └── LibroServiceTest.java
│
└── controller/ 🧱 Integración
    └── ClienteControllerTest.java
    └── VentaControllerTest.java
    └── GeneroLiterarioControllerTest.java
	└── AutorControllerTest.java
	└── LibroControllerTest.java
```
