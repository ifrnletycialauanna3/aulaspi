package ifrn.pi.eventos.models;

import java.time.LocalDate;
import java.time.LocalTime; // 1. Import do LocalTime

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Evento {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank // Para String é melhor usar @NotBlank do que @NotNull
	private String nome;

	@NotBlank
	private String local;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 2. Corrigido para yyyy-MM-dd
	private LocalDate data;

	@NotNull
	@DateTimeFormat(pattern = "HH:mm") // 3. Adicionado formato de hora
	private LocalTime horario; // 4. Alterado de LocalDate para LocalTime

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	// Getters e Setters de horario atualizados para LocalTime
	public LocalTime getHorario() {
		return horario;
	}

	public void setHorario(LocalTime horario) {
		this.horario = horario;
	}

	@Override
	public String toString() {
		return "Evento [id=" + id + ", nome=" + nome + ", local=" + local + ", data=" + data + ", horario=" + horario
				+ "]";
	}
}