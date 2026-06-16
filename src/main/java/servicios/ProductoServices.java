package edu.pucmm.eict.ormjpa.servicios;
import entidades.Producto;

public class ProductoServices extends GestionDb<Producto> {
    private static ProductoServices instancia;
    private ProductoServices() { super(Producto.class); }
    public static ProductoServices getInstancia() {
        if (instancia == null) { instancia = new ProductoServices(); }
        return instancia;
    }
}