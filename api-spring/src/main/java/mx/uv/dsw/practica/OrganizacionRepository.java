package mx.uv.dsw.practica;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio JPA de organizaciones. */
public interface OrganizacionRepository extends JpaRepository<Organizacion, Long> {
}
