package servicios;

import entidades.Producto;
import servicios.GestionDb;

public class ProductoServices extends GestionDb<Producto> {
    private static ProductoServices instancia;
    private ProductoServices() { super(Producto.class); }
    public static ProductoServices getInstancia() {
        if (instancia == null) { instancia = new ProductoServices(); }
        return instancia;
    }
}