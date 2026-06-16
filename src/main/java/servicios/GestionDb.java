package servicios;

import jakarta.persistence.*;
import java.util.List;

public class GestionDb<T> {


    private static EntityManagerFactory emf;
    private final Class<T> clase;

    public GestionDb(Class<T> clase) {
        this.clase = clase;
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
        }
    }

    public T find(Object id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(clase, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + clase.getSimpleName() + " e", clase)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void crear(T entidad) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entidad);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void eliminar(Object id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T entidad = em.find(clase, id);
            if (entidad != null) {
                em.remove(entidad);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
