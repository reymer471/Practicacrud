package controladores;

import entidades.Producto;
import servicios.ProductoServices;
import servicios.VentasServices;
import io.javalin.http.Context;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AdminControlador {

    public static void listarProductosAdmin(Context ctx) {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("productos", ProductoServices.getInstancia().findAll());
        ctx.render("productos-admin", modelo);
    }

    public static void crearProducto(Context ctx) {
        String nombre = ctx.formParam("nombre");
        String precioStr = ctx.formParam("precio");

        if (nombre != null && !nombre.isBlank()
                && precioStr != null && !precioStr.isBlank()) {

            Producto nuevo = new Producto(
                    nombre,
                    new BigDecimal(precioStr)
            );

            ProductoServices.getInstancia().crear(nuevo);
        }

        ctx.redirect("/admin/productos");
    }

    public static void eliminarProducto(Context ctx) {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        ProductoServices.getInstancia().eliminar(id);
        ctx.redirect("/admin/productos");
    }

    public static void listarVentas(Context ctx) {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("ventas", VentasServices.getInstancia().findAll());
        ctx.render("ventas", modelo);
    }
}