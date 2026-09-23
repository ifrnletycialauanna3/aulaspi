package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ifrn.pi.eventos.models.Convidado;
import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.ConvidadoRepository;
import ifrn.pi.eventos.repositories.EventoRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/eventos")
public class EventosController {

	@Autowired
	private EventoRepository er;

	@Autowired
	private ConvidadoRepository cr;

	// Rota para abrir o formulário -> http://localhost:8080/eventos/form
	@GetMapping("/form")
	public String form(Evento evento) {
		return "eventos/formEvento";
	}

	// Rota para salvar ou atualizar o evento
	@PostMapping
	public String salvar(@Valid Evento evento, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return form(evento);
		}
		
		boolean isNovo = (evento.getId() == null);
		er.save(evento);
		
		if (isNovo) {
			attributes.addFlashAttribute("mensagem", "Evento salvo com sucesso!");
		} else {
			attributes.addFlashAttribute("mensagem", "Evento atualizado com sucesso!");
		}
		
		return "redirect:/eventos";
	}

	// Rota para listar os eventos -> http://localhost:8080/eventos
	@GetMapping
	public ModelAndView listar() {
		List<Evento> eventos = er.findAll();
		ModelAndView mv = new ModelAndView("eventos/lista");
		mv.addObject("eventos", eventos);
		return mv;
	}

	// Rota para ver detalhes do evento
	@GetMapping("/{id}")
	public ModelAndView detalhes(@PathVariable Long id, Convidado convidado) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);

		if (opt.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}

		md.setViewName("eventos/detalhes");
		Evento evento = opt.get();
		md.addObject("evento", evento);

		List<Convidado> convidados = cr.findByEvento(evento);
		md.addObject("convidados", convidados);

		return md;
	}

	// Rota para salvar convidado COM VALIDAÇÃO CORRETA
	@PostMapping("/{idEvento}")
	public String salvarConvidado(@PathVariable Long idEvento, @Valid Convidado convidado, BindingResult result, RedirectAttributes attributes) {
	    
	    // 1. Se houver erro de preenchimento, interrompe antes do banco
	    if (result.hasErrors()) {
	        attributes.addFlashAttribute("mensagem", "Verifique os campos do convidado!");
	        return "redirect:/eventos/" + idEvento;
	    }

	    // 2. Garante que o evento existe
	    Optional<Evento> opt = er.findById(idEvento);
	    if (opt.isEmpty()) {
	        return "redirect:/eventos";
	    }

	    // 3. Salva apenas se passou na validação
	    Evento evento = opt.get();
	    convidado.setEvento(evento);
	    
	    boolean isNovo = (convidado.getId() == null);
	    cr.save(convidado);
	    
	    if (isNovo) {
	    	attributes.addFlashAttribute("mensagem", "Convidado adicionado com sucesso!");
	    } else {
	    	attributes.addFlashAttribute("mensagem", "Convidado atualizado com sucesso!");
	    }

	    return "redirect:/eventos/" + idEvento;
	}

	// Rota para selecionar/editar evento
	@GetMapping("/{id}/selecionar")
	public ModelAndView selecionarEvento(@PathVariable Long id) {
		ModelAndView md = new ModelAndView();
		Optional<Evento> opt = er.findById(id);

		if (opt.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}

		Evento evento = opt.get();
		md.setViewName("eventos/formEvento");
		md.addObject("evento", evento);

		return md;
	}

	// Rota para selecionar/editar convidado
	@GetMapping("/{idEvento}/convidados/{idConvidado}/selecionar")
	public ModelAndView selecionarConvidado(@PathVariable Long idEvento, @PathVariable Long idConvidado) {
		ModelAndView md = new ModelAndView();

		Optional<Evento> optEvento = er.findById(idEvento);
		Optional<Convidado> optConvidado = cr.findById(idConvidado);

		if (optEvento.isEmpty() || optConvidado.isEmpty()) {
			md.setViewName("redirect:/eventos");
			return md;
		}

		Evento evento = optEvento.get();
		Convidado convidado = optConvidado.get();

		if (!evento.getId().equals(convidado.getEvento().getId())) {
			md.setViewName("redirect:/eventos");
			return md;
		}

		md.setViewName("eventos/detalhes");
		md.addObject("convidado", convidado);
		md.addObject("evento", evento);
		md.addObject("convidados", cr.findByEvento(evento));

		return md;
	}

	// Rota para deletar evento completo
	@GetMapping("/{id}/remover")
	public String apagarEvento(@PathVariable Long id, RedirectAttributes attributes ) {
		Optional<Evento> opt = er.findById(id);

		if (!opt.isEmpty()) {
			Evento evento = opt.get();
			List<Convidado> convidados = cr.findByEvento(evento);

			cr.deleteAll(convidados);
			er.delete(evento);

			attributes.addFlashAttribute("mensagem", "Evento removido com sucesso!");
		}
		return "redirect:/eventos";
	}

	// Rota para deletar apenas um convidado por ID
	@GetMapping("/deletarConvidado/{idConvidado}")
	public String deletarConvidado(@PathVariable Long idConvidado, RedirectAttributes attributes) {
		Optional<Convidado> opt = cr.findById(idConvidado);

		if (!opt.isEmpty()) {
			Convidado convidado = opt.get();
			Evento evento = convidado.getEvento();

			cr.delete(convidado);

			attributes.addFlashAttribute("mensagem", "Convidado removido com sucesso!");

			return "redirect:/eventos/" + evento.getId();
		}

		return "redirect:/eventos";
	}
}