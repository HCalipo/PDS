package com.tasku.core;

import com.tasku.core.domain.ColorEtiqueta;
import com.tasku.core.domain.Email;
import com.tasku.core.domain.Etiqueta;
import com.tasku.core.domain.HistorialMovimientos;
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

import java.util.HashMap;
import java.util.Map;
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
		Email autor = null;
		Map<UUID, Tarjeta> tarjetas = new HashMap<>();
		Map<UUID, ListaTareas> listas = new HashMap<>();

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
						autor = new Email(args[0]);
						dueno = new Usuario(autor, args[1]);
						tablero = new Tablero(new URL(), dueno);
						tarjetas.clear();
						listas.clear();
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
						ListaTareas lista = actual.crearListaTareas();
						UUID id = lista.getId().id();
						listas.put(id, lista);
						System.out.println("Lista creada: " + id);
					}
					case "crear-tarjeta-tarea" -> {
						Tablero actual = requireTablero(tablero);
						Email correoAutor = requireAutor(autor);
						String[] args = splitArgs(rest, 3, "crear-tarjeta-tarea <titulo>|<descripcion>|<texto>");
						TarjetaTarea tarjeta = new TarjetaTarea(args[0], args[1], args[2]);
						actual.agregarTarjeta(tarjeta, correoAutor);
						UUID id = tarjeta.getId().id();
						tarjetas.put(id, tarjeta);
						System.out.println("Tarjeta tarea creada: " + id);
					}
					case "crear-tarjeta-checklist" -> {
						Tablero actual = requireTablero(tablero);
						Email correoAutor = requireAutor(autor);
						String[] args = splitArgs(rest, 2, "crear-tarjeta-checklist <titulo>|<descripcion>");
						TarjetaChecklist tarjeta = new TarjetaChecklist(args[0], args[1]);
						actual.agregarTarjeta(tarjeta, correoAutor);
						UUID id = tarjeta.getId().id();
						tarjetas.put(id, tarjeta);
						System.out.println("Tarjeta checklist creada: " + id);
					}
					case "agregar-etiqueta" -> {
						Tarjeta tarjeta = requireTarjeta(tarjetas, rest, "agregar-etiqueta <tarjetaId>|<texto>|<color>");
						String[] args = splitArgs(rest, 3, "agregar-etiqueta <tarjetaId>|<texto>|<color>");
						tarjeta.agregarEtiqueta(new Etiqueta(args[1], new ColorEtiqueta(args[2])));
						System.out.println("Etiqueta agregada.");
					}
					case "quitar-etiqueta" -> {
						Tarjeta tarjeta = requireTarjeta(tarjetas, rest, "quitar-etiqueta <tarjetaId>|<texto>|<color>");
						String[] args = splitArgs(rest, 3, "quitar-etiqueta <tarjetaId>|<texto>|<color>");
						tarjeta.quitarEtiqueta(new Etiqueta(args[1], new ColorEtiqueta(args[2])));
						System.out.println("Etiqueta removida.");
					}
					case "agregar-item" -> {
						Tarjeta tarjeta = requireTarjeta(tarjetas, rest, "agregar-item <tarjetaId>|<descripcion>");
						String[] args = splitArgs(rest, 2, "agregar-item <tarjetaId>|<descripcion>");
						TarjetaChecklist checklist = requireChecklist(tarjeta);
						checklist.agregarItemChecklist(args[1]);
						System.out.println("Item agregado.");
					}
					case "marcar-item" -> {
						Tarjeta tarjeta = requireTarjeta(tarjetas, rest, "marcar-item <tarjetaId>|<indice>|<true|false>");
						String[] args = splitArgs(rest, 3, "marcar-item <tarjetaId>|<indice>|<true|false>");
						TarjetaChecklist checklist = requireChecklist(tarjeta);
						int indice = Integer.parseInt(args[1]);
						boolean marcado = Boolean.parseBoolean(args[2]);
						checklist.marcarItemChecklist(indice, marcado);
						System.out.println("Item actualizado.");
					}
					case "agregar-tarjeta-lista" -> {
						requireTablero(tablero);
						String[] args = splitArgs(rest, 2, "agregar-tarjeta-lista <listaId>|<tarjetaId>");
						UUID listaId = parseUuid(args[0], "listaId");
						UUID tarjetaId = parseUuid(args[1], "tarjetaId");
						ListaTareas lista = requireLista(listas, listaId);
						Tarjeta tarjeta = requireTarjeta(tarjetas, tarjetaId);
						lista.agregarTarjeta(tarjeta);
						System.out.println("Tarjeta agregada a la lista.");
					}
					case "quitar-tarjeta-lista" -> {
						requireTablero(tablero);
						String[] args = splitArgs(rest, 2, "quitar-tarjeta-lista <listaId>|<tarjetaId>");
						UUID listaId = parseUuid(args[0], "listaId");
						UUID tarjetaId = parseUuid(args[1], "tarjetaId");
						ListaTareas lista = requireLista(listas, listaId);
						Tarjeta tarjeta = requireTarjeta(tarjetas, tarjetaId);
						lista.quitarTarjeta(tarjeta);
						System.out.println("Tarjeta removida de la lista.");
					}
					case "completar-tarjeta" -> {
						Tablero actual = requireTablero(tablero);
						Email correoAutor = requireAutor(autor);
						Tarjeta tarjeta = requireTarjeta(tarjetas, rest, "completar-tarjeta <tarjetaId>");
						actual.completarTarjeta(tarjeta, correoAutor);
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
						System.out.println("Tarjetas activas: " + actual.getTareas().size());
						System.out.println("Tarjetas completadas: " + actual.getListaCompletadas().getTarjetas().size());
						System.out.println("Historial registros: " + actual.getHistorial().getFirst().getMovimientos().size());
					}
					case "listar-activas" -> {
						Tablero actual = requireTablero(tablero);
						if (actual.getTareas().isEmpty()) {
							System.out.println("No hay tarjetas activas.");
							continue;
						}
						for (Tarjeta tarjeta : actual.getTareas()) {
							System.out.println(formatTarjetaResumen(tarjeta));
						}
					}
					case "listar-completadas" -> {
						Tablero actual = requireTablero(tablero);
						if (actual.getListaCompletadas().getTarjetas().isEmpty()) {
							System.out.println("No hay tarjetas completadas.");
							continue;
						}
						for (Tarjeta tarjeta : actual.getListaCompletadas().getTarjetas()) {
							System.out.println(formatTarjetaResumen(tarjeta));
						}
					}
					case "mostrar-tarjeta" -> {
						Tarjeta tarjeta = requireTarjeta(tarjetas, rest, "mostrar-tarjeta <tarjetaId>");
						printTarjetaDetalle(tarjeta);
					}
					case "mostrar-listas" -> {
						requireTablero(tablero);
						if (listas.isEmpty()) {
							System.out.println("No hay listas creadas.");
							continue;
						}
						for (Map.Entry<UUID, ListaTareas> entry : listas.entrySet()) {
							System.out.println("Lista " + entry.getKey());
							for (Tarjeta tarjeta : entry.getValue().getTarjetas()) {
								System.out.println("  " + formatTarjetaResumen(tarjeta));
							}
						}
					}
					case "mostrar-historial" -> {
						Tablero actual = requireTablero(tablero);
						HistorialMovimientos historial = actual.getHistorial().getFirst();
						if (historial.getMovimientos().isEmpty()) {
							System.out.println("Historial vacio.");
							continue;
						}
						for (Movimiento movimiento : historial.getMovimientos()) {
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
		System.out.println("  crear-tarjeta-tarea <titulo>|<descripcion>|<texto>");
		System.out.println("  crear-tarjeta-checklist <titulo>|<descripcion>");
		System.out.println("  agregar-etiqueta <tarjetaId>|<texto>|<color>");
		System.out.println("  quitar-etiqueta <tarjetaId>|<texto>|<color>");
		System.out.println("  agregar-item <tarjetaId>|<descripcion>");
		System.out.println("  marcar-item <tarjetaId>|<indice>|<true|false>");
		System.out.println("  agregar-tarjeta-lista <listaId>|<tarjetaId>");
		System.out.println("  quitar-tarjeta-lista <listaId>|<tarjetaId>");
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

	private static Email requireAutor(Email autor) {
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

	private static Tarjeta requireTarjeta(Map<UUID, Tarjeta> tarjetas, String rest, String usage) {
		String[] args = splitArgs(rest, 1, usage);
		UUID id = parseUuid(args[0], "tarjetaId");
		return requireTarjeta(tarjetas, id);
	}

	private static Tarjeta requireTarjeta(Map<UUID, Tarjeta> tarjetas, UUID id) {
		Tarjeta tarjeta = tarjetas.get(id);
		if (tarjeta == null) {
			throw new IllegalArgumentException("No existe tarjeta con id: " + id);
		}
		return tarjeta;
	}

	private static ListaTareas requireLista(Map<UUID, ListaTareas> listas, UUID id) {
		ListaTareas lista = listas.get(id);
		if (lista == null) {
			throw new IllegalArgumentException("No existe lista con id: " + id);
		}
		return lista;
	}

	private static TarjetaChecklist requireChecklist(Tarjeta tarjeta) {
		if (tarjeta instanceof TarjetaChecklist checklist) {
			return checklist;
		}
		throw new IllegalArgumentException("La tarjeta no es checklist.");
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
