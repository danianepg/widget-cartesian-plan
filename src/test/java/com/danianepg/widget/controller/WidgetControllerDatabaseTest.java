package com.danianepg.widget.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.danianepg.widget.entities.Widget;
import com.danianepg.widget.repositories.WidgetDatabaseRepository;
import com.danianepg.widget.util.WidgetMock;
import com.fasterxml.jackson.databind.ObjectMapper;

@ActiveProfiles(profiles = "db")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WebAppConfiguration
public class WidgetControllerDatabaseTest {

	private static String BASE_PATH = "http://localhost";
	private final String PATH = "/api/widgets/";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WidgetDatabaseRepository widgetRepository;

	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void findById_ok() throws Exception {

		final long id = 1L;
		final ResultActions result = this.mockMvc.perform(get(this.PATH + "/" + id));

		// @formatter:off
		result.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(id))
			.andExpect(jsonPath("_links.self.href", is(BASE_PATH + this.PATH  + id)))
			.andExpect(jsonPath("_links.widgets.href", is(BASE_PATH + this.PATH)));
		// @formatter:on

	}

	@Test
	public void findById_notFound() throws Exception {
		final long id = 100L;
		final ResultActions result = this.mockMvc.perform(get(this.PATH + "/" + id));
		result.andExpect(status().isNotFound());
	}

	@Test
	public void create_ok() throws Exception {

		final Widget widget = WidgetMock.getWidget();
		final String json = this.mapper.writeValueAsString(widget);

		final ResultActions result = this.mockMvc
				.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print());

		result.andExpect(status().is2xxSuccessful()).andExpect(jsonPath("id").isNotEmpty());

	}

	@Test
	public void create_failWhenAttributesAreNull() throws Exception {

		final Widget widget = new Widget();
		final String json = this.mapper.writeValueAsString(widget);

		final ResultActions result = this.mockMvc
				.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print());

		result.andExpect(status().isBadRequest());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void update_ok() throws Exception {

		final Widget widget = WidgetMock.getWidget();
		final String json = this.mapper.writeValueAsString(widget);

		final ResultActions resultPost = this.mockMvc
				.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print());

		final EntityModel saved = this.mapper.readValue(resultPost.andReturn().getResponse().getContentAsString(),
				EntityModel.class);

		final Map content = (Map) saved.getContent();
		final Long id = (long) (int) content.get("id");

		final Widget widgetToUpdte = new Widget(id);
		widgetToUpdte.setX(99L);
		widgetToUpdte.setY(99L);
		widgetToUpdte.setZ(99L);
		widgetToUpdte.setHeight(99f);
		widgetToUpdte.setWidth(99f);

		final String jsonToUpdte = this.mapper.writeValueAsString(widgetToUpdte);

		final ResultActions resultUpdate = this.mockMvc
				.perform(put(this.PATH + "/" + id).contentType(MediaType.APPLICATION_JSON).content(jsonToUpdte))
				.andDo(print());

		// @formatter:off
		resultUpdate.andExpect(status().is2xxSuccessful())
				.andExpect(jsonPath("id", is(id), Long.class))
				.andExpect(jsonPath("x", is(widgetToUpdte.getX()), Long.class))
				.andExpect(jsonPath("y", is(widgetToUpdte.getY()), Long.class))
				.andExpect(jsonPath("z", is(widgetToUpdte.getZ()), Long.class))
				.andExpect(jsonPath("height", is(widgetToUpdte.getHeight()), Float.class))
				.andExpect(jsonPath("width", is(widgetToUpdte.getWidth()) , Float.class));
		// @formatter:on

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void update_failWhenAttributesAreEmpty() throws Exception {

		final Widget widget = WidgetMock.getWidget();
		final String json = this.mapper.writeValueAsString(widget);

		final ResultActions resultPost = this.mockMvc
				.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print());

		final EntityModel saved = this.mapper.readValue(resultPost.andReturn().getResponse().getContentAsString(),
				EntityModel.class);

		final Map content = (Map) saved.getContent();
		final Long id = (long) (int) content.get("id");

		final Widget widgetToUpdte = new Widget(id);
		widgetToUpdte.setX(null);
		widgetToUpdte.setY(null);
		widgetToUpdte.setZ(null);
		widgetToUpdte.setHeight(null);
		widgetToUpdte.setWidth(null);

		final String jsonToUpdte = this.mapper.writeValueAsString(widgetToUpdte);

		final ResultActions resultUpdate = this.mockMvc
				.perform(put(this.PATH + "/" + id).contentType(MediaType.APPLICATION_JSON).content(jsonToUpdte))
				.andDo(print());

		resultUpdate.andExpect(status().isBadRequest());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void delete_ok() throws Exception {

		final Widget widget = WidgetMock.getWidget();
		final String json = this.mapper.writeValueAsString(widget);

		final ResultActions resultPost = this.mockMvc
				.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print());

		final EntityModel saved = this.mapper.readValue(resultPost.andReturn().getResponse().getContentAsString(),
				EntityModel.class);

		final Map content = (Map) saved.getContent();
		final Integer id = (Integer) content.get("id");

		final ResultActions resultDelete = this.mockMvc.perform(delete(this.PATH + "/" + id));
		resultDelete.andExpect(status().isOk());

		final ResultActions resultFind = this.mockMvc.perform(get(this.PATH + "/" + id));
		resultFind.andExpect(status().isNotFound());

	}

	@Test
	public void delete_notFound() throws Exception {

		final long id = 200L;
		final ResultActions result = this.mockMvc.perform(delete(this.PATH + "/" + id));
		result.andExpect(status().isNotFound());

	}

	@Test
	public void findAll_ok() throws Exception {

		final Widget widget = WidgetMock.getWidget();
		final String json = this.mapper.writeValueAsString(widget);

		this.mockMvc.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json)).andDo(print());

		// @formatter:off
		final ResultActions resultFindAll = this.mockMvc.perform(get(this.PATH)
				.param("page", "0")
				.param("sort", "y,desc"));
		// @formatter:on

		// @formatter:off
		resultFindAll.andExpect(status().isOk())
			.andExpect(jsonPath("$.*").isArray())
			.andExpect(jsonPath("_links.self.href").exists())
			.andExpect(jsonPath("_links.findById.href").exists())
			.andExpect(jsonPath("_links.filterByArea.href").exists())
			.andExpect(jsonPath("page").exists())
			.andExpect(jsonPath("_embedded.widgets").isArray());
		// @formatter:on

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void filterWidget_ok() throws Exception {

		this.widgetRepository.deleteAll();

		final List<Widget> widgets = new ArrayList<>();
		widgets.add(new Widget(50L, 50L, 11L, 100f, 100f));
		widgets.add(new Widget(50L, 100L, 12L, 100f, 100f));
		// Should not be present on return
		widgets.add(new Widget(100L, 150L, 13L, 100f, 100f));

		widgets.stream().forEach(w -> {
			try {
				final String json = this.mapper.writeValueAsString(w);
				this.mockMvc.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json));

			} catch (final Exception e) {
				fail("It was not possible to insert records.");
			}
		});

		// @formatter:off
		final ResultActions resultFilterByArea = this.mockMvc.perform(get(this.PATH +"/filter")
				.param("lowerX", "0")
				.param("lowerY", "0")
				.param("upperX", "100")
				.param("upperY", "150")
				.param("page", "0")
				.param("sort", "x,desc"));
		// @formatter:on

		final EntityModel saved = this.mapper
				.readValue(resultFilterByArea.andReturn().getResponse().getContentAsString(), EntityModel.class);

		final Map content = (Map) saved.getContent();
		final Map embedded = (Map) content.get("_embedded");
		final List<Widget> widgetsSaved = (List<Widget>) embedded.get("widgets");

		resultFilterByArea.andExpect(status().isOk());
		assertThat(widgetsSaved.size()).isEqualTo(2);

	}

	@Test
	public void filterWidget_notFound() throws Exception {

		this.widgetRepository.deleteAll();

		final List<Widget> widgets = new ArrayList<>();
		widgets.add(new Widget(50L, 50L, 11L, 100f, 100f));
		widgets.add(new Widget(50L, 100L, 12L, 100f, 100f));
		widgets.add(new Widget(100L, 150L, 13L, 100f, 100f));

		widgets.stream().forEach(w -> {
			try {
				final String json = this.mapper.writeValueAsString(w);
				this.mockMvc.perform(post(this.PATH).contentType(MediaType.APPLICATION_JSON).content(json));

			} catch (final Exception e) {
				fail("It was not possible to insert records.");
			}
		});

		// @formatter:off
		final ResultActions resultFilterByArea = this.mockMvc.perform(get(this.PATH +"/filter")
				.param("lowerX", "0")
				.param("lowerY", "0")
				.param("upperX", "0")
				.param("upperY", "0")
				.param("page", "0")
				.param("sort", "z,desc"));
		// @formatter:on

		resultFilterByArea.andExpect(status().isOk()).andExpect(jsonPath("_embedded").doesNotExist());

	}
}
