package com.danianepg.widget.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.danianepg.widget.entities.Widget;

/**
 * Interface to direct services to the right repository. When the application is
 * started with the profile 'db', the application will delegate the storage
 * responsibility to Spring Repository
 * {@link com.danianepg.widget.repositories.WidgetDatabaseRepository} and the
 * data will be stored in a SQL
 * database. When no profile or 'inmemory' is informed, the storage is done in
 * memory and it is responsibility of
 * {@link com.danianepg.widget.repositories.WidgetInMemoryRepository}.
 *
 * @author Daniane P. Gomes
 *
 */
@Service
public interface WidgetRepository {

	Optional<Widget> findById(final Long id);

	void deleteById(final Long id);

	Page<Widget> findAll(final Pageable pageable);

	Map<Long, Widget> saveAll(final List<Widget> widgetsToMoveLst);

	Widget save(@Valid final Widget widget);

	Optional<Widget> findByZ(final Long currentZ);

}
