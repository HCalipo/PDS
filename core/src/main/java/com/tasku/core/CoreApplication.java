package com.tasku.core;

import com.tasku.core.domain.ColorEtiqueta;
import com.tasku.core.domain.Email;
import com.tasku.core.domain.Etiqueta;
import com.tasku.core.domain.ListaItems;
import com.tasku.core.domain.ListaTareas;
import com.tasku.core.domain.Movimiento;
import com.tasku.core.domain.Tablero;
import com.tasku.core.domain.Tarjeta;
import com.tasku.core.domain.TarjetaChecklist;
import com.tasku.core.domain.TarjetaTarea;
import com.tasku.core.domain.URL;
import com.tasku.core.domain.Usuario;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class CoreApplication {

	public static void main(String[] args) {
		runCli();
	}

	private static void runCli() {
		System.out.println("=== CLI Tasku ===");
		printHelp();

		Scanner scanner = new Scanner(System.in);
		Tablero tablero = null;
		Usuario dueno = null;

		while (true) {
			System.out.print("> ");
			if (!scanner.hasNextLine()) {
				break;
			}

			String line = scanner.nextLine().trim();
			if (line.isEmpty()) {
				continue;
			}

			String lower = line.toLowerCase();
			if ("salir".equals(lower)) {
				break;
			}
			if ("ayuda".equals(lower)) {
				printHelp();
				continue;
			}

			String[] parts = line.split(" ", 2);
			String cmd = parts[0].toLowerCase();
			String rest = parts.length > 1 ? parts[1].trim() : "";

			try {
				switch (cmd) {
					case "crear-tablero" -> {
						String[] args = splitArgs(rest, 2, "crear-tablero <email>|<nombre>");
						Email correo = new Email(args[0]);
						dueno = new Usuario(correo, args[1]);
						tablero = new Tablero(new URL(), dueno);
						System.out.println("Tablero creado: " + tablero.getUrl().url());
					}
					case "agregar-colaborador" -> {
						Tablero actual = requireTablero(tablero);
						String[] args = splitArgs(rest, 2, "agregar-colaborador <email>|<nombre>");
						actual.agregarColaborador(new Usuario(new Email(args[0]), args[1]));
						System.out.println("Colaborador agregado.");
					}
					case "crear-lista" -> {
						Tablero actual = requireTablero(tablero);
						UUID id = actual.crearListaTareas();
						System.out.println("Lista creada: " + id);
					}
					case "crear-tarjeta-tarea" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 4, "crear-tarjeta-tarea <listaId>|<titulo>|<descripcion>|<texto>");
						UUID listaId = parseUuid(args[0], "listaId");
						UUID id = actual.crearTarjetaTarea(listaId, args[1], args[2], args[3], autor);
						System.out.println("Tarjeta tarea creada: " + id);
					}
					case "crear-tarjeta-checklist" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 3, "crear-tarjeta-checklist <listaId>|<titulo>|<descripcion>");
						UUID listaId = parseUuid(args[0], "listaId");
						UUID id = actual.crearTarjetaChecklist(listaId, args[1], args[2], autor);
						System.out.println("Tarjeta checklist creada: " + id);
					}
					case "agregar-etiqueta" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 3, "agregar-etiqueta <tarjetaId>|<texto>|<color>");
						UUID tarjetaId = parseUuid(args[0], "tarjetaId");
						actual.agregarEtiqueta(tarjetaId, args[1], new ColorEtiqueta(args[2]), autor);
						System.out.println("Etiqueta agregada.");
					}
					case "quitar-etiqueta" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 3, "quitar-etiqueta <tarjetaId>|<texto>|<color>");
						UUID tarjetaId = parseUuid(args[0], "tarjetaId");
						actual.quitarEtiqueta(tarjetaId, args[1], new ColorEtiqueta(args[2]), autor);
						System.out.println("Etiqueta removida.");
					}
					case "agregar-item" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 2, "agregar-item <tarjetaId>|<descripcion>");
						UUID tarjetaId = parseUuid(args[0], "tarjetaId");
						actual.agregarItemChecklist(tarjetaId, args[1], autor);
						System.out.println("Item agregado.");
					}
					case "marcar-item" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 3, "marcar-item <tarjetaId>|<indice>|<true|false>");
						UUID tarjetaId = parseUuid(args[0], "tarjetaId");
						int indice = Integer.parseInt(args[1]);
						boolean marcado = Boolean.parseBoolean(args[2]);
						actual.marcarItemChecklist(tarjetaId, indice, marcado, autor);
						System.out.println("Item actualizado.");
					}
					case "completar-tarjeta" -> {
						Tablero actual = requireTablero(tablero);
						Usuario autor = requireAutor(dueno);
						String[] args = splitArgs(rest, 1, "completar-tarjeta <tarjetaId>");
						UUID tarjetaId = parseUuid(args[0], "tarjetaId");
						actual.completarTarjeta(tarjetaId, autor);
						System.out.println("Tarjeta completada.");
					}
					case "bloquear" -> {
						requireTablero(tablero).bloquear();
						System.out.println("Tablero bloqueado.");
					}
					case "desbloquear" -> {
						requireTablero(tablero).desbloquear();
						System.out.println("Tablero desbloqueado.");
					}
					case "mostrar-tablero" -> {
						Tablero actual = requireTablero(tablero);
						System.out.println("Tablero: " + actual.getUrl().url());
						System.out.println("Dueno: " + actual.getDueno().getNombre() + " <" + actual.getDueno().getCorreo().email() + ">");
						System.out.println("Bloqueado: " + actual.isEstaBloqueado());
						System.out.println("Colaboradores: " + actual.getColaboradores().size());
						System.out.println("Listas: " + actual.getListasTareas().size());
						System.out.println("Tarjetas activas: " + actual.getTarjetasActivas().size());
						System.out.println("Tarjetas completadas: " + actual.getTarjetasCompletadas().size());
						System.out.println("Historial registros: " + actual.getHistorial().size());
					}
					case "listar-activas" -> {
						Tablero actual = requireTablero(tablero);
						if (actual.getTarjetasActivas().isEmpty()) {
							System.out.println("No hay tarjetas activas.");
							continue;
						}
						for (Tarjeta tarjeta : actual.getTarjetasActivas()) {
							System.out.println(formatTarjetaResumen(tarjeta));
						}
					}
					case "listar-completadas" -> {
						Tablero actual = requireTablero(tablero);
						if (actual.getTarjetasCompletadas().isEmpty()) {
							System.out.println("No hay tarjetas completadas.");
							continue;
						}
						for (Tarjeta tarjeta : actual.getTarjetasCompletadas()) {
							System.out.println(formatTarjetaResumen(tarjeta));
						}
					}
					case "mostrar-tarjeta" -> {
						Tablero actual = requireTablero(tablero);
						String[] args = splitArgs(rest, 1, "mostrar-tarjeta <tarjetaId>");
						UUID tarjetaId = parseUuid(args[0], "tarjetaId");
						Tarjeta tarjeta = actual.obtenerTarjeta(tarjetaId);
						printTarjetaDetalle(tarjeta);
					}
					case "mostrar-listas" -> {
						Tablero actual = requireTablero(tablero);
						if (actual.getListasTareas().isEmpty()) {
							System.out.println("No hay listas creadas.");
							continue;
						}
						for (ListaTareas lista : actual.getListasTareas()) {
							System.out.println("Lista " + lista.getId().id());
							for (Tarjeta tarjeta : lista.getTarjetas()) {
								System.out.println("  " + formatTarjetaResumen(tarjeta));
							}
						}
					}
					case "mostrar-historial" -> {
						Tablero actual = requireTablero(tablero);
						if (actual.getHistorial().isEmpty()) {
							System.out.println("Historial vacio.");
							continue;
						}
						for (Movimiento movimiento : actual.getHistorial()) {
							System.out.println(movimiento.getFechaHora() + " | " + movimiento.getAutor().email() + " | " + movimiento.getAccionDetalle());
						}
					}
					default -> System.out.println("Comando no reconocido. Escribe 'ayuda' para ver opciones.");
				}
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
			}
		}

		System.out.println("Saliendo...");
	}

	private static void printHelp() {
		System.out.println("Comandos disponibles (usa '|' para separar argumentos):");
		System.out.println("  crear-tablero <email>|<nombre>");
		System.out.println("  agregar-colaborador <email>|<nombre>");
		System.out.println("  crear-lista");
		System.out.println("  crear-tarjeta-tarea <listaId>|<titulo>|<descripcion>|<texto>");
		System.out.println("  crear-tarjeta-checklist <listaId>|<titulo>|<descripcion>");
		System.out.println("  agregar-etiqueta <tarjetaId>|<texto>|<color>");
		System.out.println("  quitar-etiqueta <tarjetaId>|<texto>|<color>");
		System.out.println("  agregar-item <tarjetaId>|<descripcion>");
		System.out.println("  marcar-item <tarjetaId>|<indice>|<true|false>");
		System.out.println("  completar-tarjeta <tarjetaId>");
		System.out.println("  bloquear | desbloquear");
		System.out.println("  mostrar-tablero | listar-activas | listar-completadas");
		System.out.println("  mostrar-tarjeta <tarjetaId>");
		System.out.println("  mostrar-listas | mostrar-historial");
		System.out.println("  ayuda | salir");
	}

	private static String[] splitArgs(String rest, int expected, String usage) {
		if (rest == null || rest.isBlank()) {
			throw new IllegalArgumentException("Uso: " + usage);
		}
		String[] parts = rest.split("\\|", -1);
		if (parts.length != expected) {
			throw new IllegalArgumentException("Uso: " + usage);
		}
		for (int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}
		return parts;
	}

	private static Tablero requireTablero(Tablero tablero) {
		if (tablero == null) {
			throw new IllegalStateException("Primero crea un tablero con 'crear-tablero'.");
		}
		return tablero;
	}

	private static Usuario requireAutor(Usuario autor) {
		if (autor == null) {
			throw new IllegalStateException("Debes crear un tablero antes de realizar acciones.");
		}
		return autor;
	}

	private static UUID parseUuid(String value, String label) {
		try {
			return UUID.fromString(value.trim());
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException("UUID invalido para " + label + ": " + value);
		}
	}

	private static String formatTarjetaResumen(Tarjeta tarjeta) {
		String tipo = tarjeta instanceof TarjetaChecklist ? "Checklist" : "Tarea";
		return tipo + " " + tarjeta.getId().id() + " | " + tarjeta.getTitulo() + " | " + tarjeta.getDescripcion();
	}

	private static void printTarjetaDetalle(Tarjeta tarjeta) {
		System.out.println(formatTarjetaResumen(tarjeta));
		System.out.println("Completada: " + tarjeta.isEstaCompletada());
		Set<Etiqueta> etiquetas = tarjeta.getEtiquetas();
		if (etiquetas.isEmpty()) {
			System.out.println("Etiquetas: (sin etiquetas)");
		} else {
			System.out.println("Etiquetas:");
			for (Etiqueta etiqueta : etiquetas) {
				System.out.println("  " + etiqueta.getTexto() + " [" + etiqueta.getColor().color() + "]");
			}
		}

		if (tarjeta instanceof TarjetaChecklist checklist) {
			ListaItems items = checklist.getListaItems();
			if (items.getItems().isEmpty()) {
				System.out.println("Checklist: (sin items)");
			} else {
				System.out.println("Checklist:");
				for (int i = 0; i < items.getItems().size(); i++) {
					var item = items.getItems().get(i);
					System.out.println("  [" + i + "] " + (item.estaMarcado() ? "[x] " : "[ ] ") + item.descripcion());
				}
			}
		}

		if (tarjeta instanceof TarjetaTarea tarea) {
			System.out.println("Texto: " + tarea.getTexto());
		}
	}

}
