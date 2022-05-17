package tacos.web;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;

@Slf4j
@Controller
@RequestMapping("/ingredient")
public class IngredientController {
	RestTemplate rest = new RestTemplate();

	@ModelAttribute
	public void addIngredientToModel(Model model) {
		List<Ingredient> ingredients = Arrays
				.asList(rest.getForObject("http://localhost:8080/ingredients", Ingredient[].class));
		model.addAttribute("ingredients", ingredients);
	}

	@GetMapping
	public String showIngredient(Model model) {
		return "ingredient_detail";
	}

	@GetMapping("/add")
	public String showAddForm(Model model) {
		model.addAttribute("ingredient", new Ingredient());
		return "addIngredient";
	}

	@PostMapping
	public String addIngredient(Ingredient ingredient, Model model) {
		model.addAttribute(ingredient);
		log.info("Ingredient saved: " + ingredient);
		rest.postForObject("http://localhost:8080/ingredients", ingredient, Ingredient.class);

		return "addIngredientSuccess";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editProduct(@RequestParam("id") String id, Model model) {
		Ingredient ingredient = rest.getForObject("http://localhost:8080/ingredients/id", Ingredient.class, id);
		model.addAttribute("ingredient", ingredient);
		log.info("Edit: " + id);
		return "addIngredient";
	}

	@RequestMapping(value = "/preDelete", method = RequestMethod.GET)
	public String preDeleteProduct(@RequestParam(required = false, name = "id") String id, Model model) {

		return "confirm";
	}

	@RequestMapping(value = "delete", method = RequestMethod.DELETE)
	public String deleteProduct(@RequestParam("id") String id, RedirectAttributes model) {
		rest.delete("http://localhost:8080/ingredients/{id}");
		return "redirect:/ingredient";
	}

	@RequestMapping(value = "save", method = RequestMethod.PUT)
	public String save(@Valid Ingredient product, Errors errors, RedirectAttributes model) {
		if (errors.hasErrors()) {
			return "addIngredient";
		} else {
			Ingredient ingredient = (Ingredient) model.getAttribute("ingredient");
			rest.put("http://localhost:8080/ingredients/{id}", ingredient, ingredient.getId());
			return "redirect:/ingredient";
		}
	}
}