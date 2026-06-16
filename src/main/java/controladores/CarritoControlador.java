package controladores;

import entidades.ItemCarrito;
import entidades.Producto;
import entidades.VentasProductos;
import servicios.ProductoServices;
import servicios.VentasServices;
import io.javalin.http.Context;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarritoControlador {

    private static List<ItemCarrito> obtenerCarrito(Context ctx) {
        List<ItemCarrito> carrito = ctx.sessionAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            ctx.sessionAttribute("carrito", carrito);
        }
        return carrito;
    }

    public static void listarProductos(Context ctx) {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("productos", ProductoServices.getInstancia().findAll());

        List<ItemCarrito> carrito = obtenerCarrito(ctx);
        int totalArticulos = carrito.stream().mapToInt(ItemCarrito::getCantidad).sum();
        modelo.put("cantidadCarrito", totalArticulos);

        ctx.render("/templates/tienda.html", modelo);
    }

    public static void agregarAlCarrito(Context ctx) {
        Long idProducto = Long.parseLong(ctx.formParam("idProducto"));
        int cantidad = Integer.parseInt(ctx.formParam("cantidad"));

        Producto producto = ProductoServices.getInstancia().find(idProducto);
        if (producto != null && cantidad > 0) {
            List<ItemCarrito> carrito = obtenerCarrito(ctx);
            boolean encontrado = false;

            for (ItemCarrito item : carrito) {
                if (item.getProducto().getId().equals(idProducto)) {
                    item.setCantidad(item.getCantidad() + cantidad);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                carrito.add(new ItemCarrito(producto, cantidad));
            }
        }
        ctx.redirect("/");
    }

    public static void verCarrito(Context ctx) {
        Map<String, Object> modelo = new HashMap<>();
        List<ItemCarrito> carrito = obtenerCarrito(ctx);

        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrito item : carrito) {
            BigDecimal subtotal = item.getProducto().getPrecio()
                    .multiply(new BigDecimal(item.getCantidad()));
            total = total.add(subtotal);
        }

        modelo.put("carrito", carrito);
        modelo.put("total", total);
        ctx.render("/templates/carrito.html", modelo);
    }

    // requerimiento 8: eliminar un item individual del carrito
    public static void eliminarDelCarrito(Context ctx) {
        Long idProducto = ctx.pathParamAsClass("id", Long.class).get();
        List<ItemCarrito> carrito = obtenerCarrito(ctx);
        carrito.removeIf(item -> item.getProducto().getId().equals(idProducto));
        ctx.redirect("/carrito");
    }

    public static void procesarCompra(Context ctx) {
        String nombreCliente = ctx.formParam("nombreCliente");
        List<ItemCarrito> carrito = obtenerCarrito(ctx);

        if (nombreCliente != null && !nombreCliente.isBlank() && !carrito.isEmpty()) {
            // guardamos una entrada por producto (sin duplicados) - la cantidad va en el nombre
            List<Producto> productosComprados = new ArrayList<>();
            for (ItemCarrito item : carrito) {
                productosComprados.add(item.getProducto());
            }

            VentasProductos nuevaVenta = new VentasProductos(nombreCliente, productosComprados);
            VentasServices.getInstancia().crear(nuevaVenta);

            ctx.sessionAttribute("carrito", new ArrayList<ItemCarrito>());
        }
        ctx.redirect("/");
    }

    public static void limpiarCarrito(Context ctx) {
        ctx.sessionAttribute("carrito", new ArrayList<ItemCarrito>());
        ctx.redirect("/carrito");
    }
}
