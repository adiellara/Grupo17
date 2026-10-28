package com.ues.edu.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ues.edu.entities.User;
import com.ues.edu.services.UserServices;
/**
 * Modulo Usuario
 *
 * @author Cristian Rodolfo Aguirre González 
 * 
 */
@Controller
public class UserController
{
	@Autowired
	private UserServices services;

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user,
			@RequestParam("confirmPassword") String confirmPassword,
			Model model,
			RedirectAttributes redirectAttributes)
	{
		if (user.getUname() == null || user.getUname().trim().isEmpty()
				|| user.getUemail() == null || user.getUemail().trim().isEmpty()
				|| user.getUpassword() == null || user.getUpassword().trim().isEmpty()
				|| user.getUnumber() == null) {
			model.addAttribute("error", "Completa todos los campos para crear tu cuenta.");
			model.addAttribute("user", user);
			return "register";
		}

		if (user.getUpassword().length() < 4) {
			model.addAttribute("error", "La contraseña debe tener al menos 4 caracteres.");
			model.addAttribute("user", user);
			return "register";
		}

		if (!user.getUpassword().equals(confirmPassword)) {
			model.addAttribute("error", "Las contraseñas no coinciden.");
			model.addAttribute("user", user);
			return "register";
		}

		if (this.services.emailExists(user.getUemail())) {
			model.addAttribute("error", "Este correo ya está registrado. Inicia sesión o usa otro.");
			model.addAttribute("user", user);
			return "register";
		}

		this.services.addUser(user);
		redirectAttributes.addFlashAttribute("success", "Cuenta creada. Ya puedes iniciar sesión.");
		return "redirect:/login";
	}

	@PostMapping("/addingUser")
	public String  addUser(@ModelAttribute User user)
	{
		System.out.println(user);
		this.services.addUser(user);
		return "redirect:/admin/services";
	}

	@GetMapping("/updatingUser/{id}")
	public String updateUser(@ModelAttribute User user, @PathVariable("id") int id)
	{
		this.services.updateUser(user, id);
		return "redirect:/admin/services";
	}

	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable("id" )int id)
	{
		this.services.deleteUser(id);
		return "redirect:/admin/services";
	}
	


}
