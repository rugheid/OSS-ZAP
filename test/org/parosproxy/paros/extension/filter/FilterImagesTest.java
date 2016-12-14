package org.parosproxy.paros.extension.filter;

import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.imageFilterActions.ImageFilterAction;
import org.parosproxy.paros.model.Model;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;

public class FilterImagesTest {

	private FilterImages filterImages;


	@Before
	public void setUp() throws Exception {
		filterImages = new FilterImages();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInit() throws NoSuchFieldException, IllegalAccessException {
		Field imageFilterActionsField = FilterImages.class.getDeclaredField("imageFilterActions");
		imageFilterActionsField.setAccessible(true);
		List<ImageFilterAction> actions = (List<ImageFilterAction>) imageFilterActionsField.get(filterImages);
		assertTrue(actions == null || actions.size() == 0);

		filterImages.init(null);

		actions = (List<ImageFilterAction>) imageFilterActionsField.get(filterImages);
		assertTrue(actions.size() > 0);
	}

	// FIXME: These two tests don't work because of the i18n (ResourceBundle)
	@Test
	public void testName() {
		String name = filterImages.getName();
		assertTrue(name != null && !name.isEmpty());
	}

	@Test
	public void testUniqueID() {
		FilterFactory factory = new FilterFactory();
		factory.loadAllFilter();
		List<Filter> filters = factory.getAllFilter();
		int id = filterImages.getId();
		for (Filter filter: filters) {
			assertNotEquals(filter.getId(), id);
		}
	}

}
