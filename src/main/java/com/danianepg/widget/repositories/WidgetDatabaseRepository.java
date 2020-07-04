package com.danianepg.widget.repositories;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.danianepg.widget.entities.Widget;

/**
 * Spring repository to handle {@link com.danianepg.widget.entities.Widget}
 * entities.
 *
 * @author Daniane P. Gomes
 *
 */
@RepositoryRestResource(exported = false)
public interface WidgetDatabaseRepository extends PagingAndSortingRepository<Widget, Long> {

	Optional<Widget> findByZ(@Param("currentZ") Long currentZ);

}
