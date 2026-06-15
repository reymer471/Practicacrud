public class ItemCarrito {package edu.pucmm.eict.ormjpa.entidades;

    // representará el Carrito en la memoria HTTP sesion
    public class ItemCarrito {
        private Producto producto;
        private int cantidad;

        public ItemCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }
        // Generar Getters y Setters...
        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
    }

}
