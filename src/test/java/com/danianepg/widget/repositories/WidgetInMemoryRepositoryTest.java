package com.danianepg.widget.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.services.inmemory.WidgetsInMemoryStorageService;
import com.danianepg.widget.util.WidgetMock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WidgetInMemoryRepositoryTest {

	@Autowired
	private WidgetInMemoryRepository repository;

	@MockBean
	private WidgetsInMemoryStorageService widgets;

	@Test
	public void findById_ok() {
		when(this.widgets.getWidgets()).thenReturn(WidgetMock.getWidgetsMock());
		assertThat(this.repository.findById(1L)).isPresent();
	}

	@Test
	public void findById_notFound() {
		when(this.widgets.getWidgets()).thenReturn(WidgetMock.getWidgetsMock());
		assertThat(this.repository.findById(10L)).isNotPresent();

	}

	@Test
	public void deleteById_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);
		this.repository.deleteById(1L);

		assertThat(widgets.get(1L)).isNull();

	}

	@Test(expected = Test.None.class)
	public void deleteById_unexistendId_ok() {
		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);
		this.repository.deleteById(1L);
	}

	@Test
	public void findAll_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		assertTrue(this.repository.findAll(null).hasContent());
		assertThat(this.repository.findAll(null).getTotalElements()).isEqualTo(widgets.size());

	}

	@Test
	public void findAll_emptyList() {
		when(this.widgets.getWidgets()).thenReturn(new ConcurrentHashMap<>());
		assertThat(this.repository.findAll(null).getContent()).isEmpty();
		assertThat(this.repository.findAll(null)).isInstanceOf(PageImpl.class);
	}

	@Test
	public void findAll_withPages() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		final int pageSize = 2;
		final Pageable page = PageRequest.of(0, pageSize);
		final int numberOfPages = (int) Math.ceil((double) widgets.size() / pageSize);

		assertThat(this.repository.findAll(page)).hasSize(pageSize);
		assertThat(this.repository.findAll(page).getTotalPages()).isEqualTo(numberOfPages);

	}

	@Test
	public void findAll_orderByIdAsc() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		final int pageSize = 2;
		final Pageable page = PageRequest.of(0, pageSize, Sort.by("id"));

		assertThat(this.repository.findAll(page).getContent().get(0).getId()).isEqualTo(1L);

	}

	@Test
	public void findAll_orderByIdDesc() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		final int pageSize = 2;
		final Pageable page = PageRequest.of(0, pageSize, Sort.by("id").descending());

		final long greatestId = Collections.max(widgets.keySet());
		assertThat(this.repository.findAll(page).getContent().get(0).getId()).isEqualTo(greatestId);

	}

	@Test
	public void saveAll_ok() {

		final List<Widget> widgets = new ArrayList<>();

		final Widget widget1 = new Widget(2L, 1L, 1L, 10f, 10f);
		widgets.add(widget1);

		final Widget widget2 = new Widget(1L, 2L, 6L, 10f, 10f);
		widgets.add(widget2);

		when(this.widgets.getWidgets()).thenCallRealMethod();
		final Map<Long, Widget> savedWidgets = this.repository.saveAll(widgets);

		assertThat(savedWidgets).hasSameSizeAs(widgets).containsKeys(1L, 2L);

	}

	@Test
	public void findByZ_ok() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		final Widget widget = widgets.get(1L);
		assertThat(this.repository.findByZ(widget.getZ())).contains(widget);

	}

	@Test
	public void findByZ_notFound() {

		final Map<Long, Widget> widgets = WidgetMock.getWidgetsMock();
		when(this.widgets.getWidgets()).thenReturn(widgets);

		assertThat(this.repository.findByZ(20L)).isEmpty();

	}

}
