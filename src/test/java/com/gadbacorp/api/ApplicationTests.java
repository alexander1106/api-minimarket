package com.gadbacorp.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private com.gadbacorp.api.config.PermisosService permisosService;

	@Autowired
	private com.gadbacorp.api.repository.seguridad.RolModuloRepository rolModuloRepository;

	@Autowired
	private com.gadbacorp.api.repository.seguridad.RolRepository rolRepository;

	@Autowired
	private com.gadbacorp.api.repository.empleados.UsuarioRepository usuarioRepository;

	@Autowired
	private com.gadbacorp.api.repository.administrable.EmpresasRepository empresasRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void endpointProtegidoSinTokenNoEsAccesible() throws Exception {
		mockMvc.perform(get("/api/minimarket/ventas"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void endpointProtegidoConTokenInvalidoNoEsAccesible() throws Exception {
		mockMvc.perform(get("/api/minimarket/ventas").header("Authorization", "Bearer token-falso"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void usuariosRequiereTokenParaListar() throws Exception {
		mockMvc.perform(get("/api/minimarket/usuarios"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void swaggerYHealthSonPublicos() throws Exception {
		mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
		mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
	}

	@Test
	void loginConCredencialesInexistentesFalla() throws Exception {
		mockMvc.perform(post("/api/minimarket/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"nadie\",\"password\":\"x\"}"))
				.andExpect(status().is4xxClientError());
	}

	private org.springframework.test.web.servlet.request.RequestPostProcessor como(String rol) {
		return user("usuario-" + rol).authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority(rol));
	}

	@Test
	void crearUsuariosYaNoEsPublico() throws Exception {
		mockMvc.perform(post("/api/minimarket/usuarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void cajeroNoPuedeCrearEmpresas() throws Exception {
		mockMvc.perform(post("/api/minimarket/empresas").with(como("CAJERO")))
				.andExpect(status().isForbidden());
	}

	@Test
	void almaceneroNoPuedeRegistrarVentas() throws Exception {
		mockMvc.perform(post("/api/minimarket/ventas").with(como("ALMACENERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void cajeroNoPuedeEditarProductos() throws Exception {
		mockMvc.perform(put("/api/minimarket/productos").with(como("CAJERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void cajeroSiPuedeLlegarAlRegistroDeVentas() throws Exception {
		// pasa la autorizacion y la validacion de negocio responde 400 por el cuerpo vacio
		mockMvc.perform(post("/api/minimarket/ventas").with(como("CAJERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void cualquierRolAutenticadoPuedeConsultar() throws Exception {
		mockMvc.perform(get("/api/minimarket/productos").with(como("CAJERO"))).andExpect(status().isOk());
		mockMvc.perform(get("/api/minimarket/aperturas-caja/cajas-abiertas").with(como("CAJERO"))).andExpect(status().isOk());
	}

	@Test
	void rutasAlternativasDelFrontendExisten() throws Exception {
		// alias de las rutas que el frontend usa con otra grafia
		mockMvc.perform(get("/api/minimarket/aperturas-cajas/cajas-abiertas").with(como("CAJERO"))).andExpect(status().isOk());
		mockMvc.perform(get("/api/minimarket/aperturas-caja/usuario/1/abierta").with(como("CAJERO"))).andExpect(status().isOk());
	}

	@Test
	void pdfDeVentaInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/api/minimarket/ventas/9999/pdf").with(como("CAJERO")))
				.andExpect(status().isNotFound());
	}

	@Test
	void recuperacionDeContrasenaEsPublicaYNoRevelaSiElCorreoExiste() throws Exception {
		mockMvc.perform(post("/api/minimarket/enviar-link-recuperacion")
				.contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"nadie@correo.com\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void resetPasswordConTokenInvalidoDevuelve400() throws Exception {
		mockMvc.perform(post("/api/minimarket/reset-password")
				.contentType(MediaType.APPLICATION_JSON).content("{\"token\":\"x\",\"nuevaClave\":\"123456\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void cotizacionInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/api/minimarket/cotizaciones/9999").with(como("CAJERO")))
				.andExpect(status().isNotFound());
	}

	@Test
	void usuarioAlezPuedeIniciarSesionComoSuperadmin() throws Exception {
		String respuesta = mockMvc.perform(post("/api/minimarket/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"alez\",\"password\":\"1234\"}"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		org.junit.jupiter.api.Assertions.assertTrue(respuesta.contains("\"rol\":\"SUPERADMIN\""), respuesta);
		org.junit.jupiter.api.Assertions.assertTrue(respuesta.contains("\"token\":\""), respuesta);
		org.junit.jupiter.api.Assertions.assertFalse(respuesta.contains("\"password\""), "el hash no debe salir en el JSON");
	}

	@Test
	void conElTokenDeAlezSePuedeConsultarYEscribir() throws Exception {
		String respuesta = mockMvc.perform(post("/api/minimarket/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"alez\",\"password\":\"1234\"}"))
				.andReturn().getResponse().getContentAsString();
		String token = respuesta.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");

		mockMvc.perform(get("/api/minimarket/roles").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(6));
		mockMvc.perform(get("/api/minimarket/modulos").header("Authorization", "Bearer " + token))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(11));
		// SUPERADMIN puede escribir en todos los modulos: la autorizacion pasa (no 401/403) y falla por el cuerpo
		int codigo = mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token))
				.andReturn().getResponse().getStatus();
		org.junit.jupiter.api.Assertions.assertTrue(codigo != 401 && codigo != 403, "codigo: " + codigo);

		// y el JSON mal formado ahora es 400, no 500
		String tokenEmpresa = tokenDe("empresa", "1234");
		mockMvc.perform(post("/api/minimarket/clientes").header("Authorization", "Bearer " + tokenEmpresa)
				.contentType(MediaType.APPLICATION_JSON).content("{no es json"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void loginConClaveIncorrectaDeAlezDevuelve401() throws Exception {
		mockMvc.perform(post("/api/minimarket/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"alez\",\"password\":\"mala\"}"))
				.andExpect(status().isUnauthorized());
	}

	private String tokenDe(String usuario, String clave) throws Exception {
		String respuesta = mockMvc.perform(post("/api/minimarket/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + usuario + "\",\"password\":\"" + clave + "\"}"))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return respuesta.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");
	}

	@Test
	void superadminNoAccedeALosModulosOperativos() throws Exception {
		String token = tokenDe("alez", "1234");
		for (String ruta : new String[] {"productos", "ventas", "clientes", "cajas", "inventario", "delivery", "compras", "aperturas-cajas"}) {
			mockMvc.perform(get("/api/minimarket/" + ruta).header("Authorization", "Bearer " + token))
					.andExpect(status().isForbidden());
		}
		mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void superadminSiAdministraEmpresasSucursalesYRoles() throws Exception {
		String token = tokenDe("alez", "1234");
		mockMvc.perform(get("/api/minimarket/empresas").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
		mockMvc.perform(get("/api/minimarket/sucursales").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
		mockMvc.perform(get("/api/minimarket/roles").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
	}

	@Test
	void usuarioEmpresaAccedeATodoLoOperativoPeroNoAAdministrable() throws Exception {
		String token = tokenDe("empresa", "1234");
		for (String ruta : new String[] {"productos", "ventas", "clientes", "cajas", "inventario", "delivery", "compras"}) {
			mockMvc.perform(get("/api/minimarket/" + ruta).header("Authorization", "Bearer " + token))
					.andExpect(status().is2xxSuccessful());
		}
		// escribe en ventas (llega a la validacion de negocio) pero no administra empresas
		mockMvc.perform(post("/api/minimarket/ventas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden());
	}

	@Test
	void elLoginDevuelveLosModulosDelRol() throws Exception {
		String alez = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"alez\",\"password\":\"1234\"}"))
				.andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertTrue(alez.contains("\"modulos\":[\"Administrable\",\"Seguridad\"]"), alez);

		String empresa = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"empresa\",\"password\":\"1234\"}"))
				.andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertFalse(empresa.contains("Administrable"), empresa);
		org.junit.jupiter.api.Assertions.assertTrue(empresa.contains("\"Ventas\"") && empresa.contains("\"Caja\"")
				&& empresa.contains("\"Dashboard\"") && empresa.contains("\"Seguridad\""), empresa);
	}

	@Test
	void misModulosDevuelveElMenuDelUsuarioAutenticado() throws Exception {
		String token = tokenDe("alez", "1234");
		mockMvc.perform(get("/api/minimarket/mis-modulos").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(2))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0]").value("Administrable"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[1]").value("Seguridad"));
		mockMvc.perform(get("/api/minimarket/mis-modulos")).andExpect(status().is4xxClientError());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void quitarUnModuloDeLaTablaQuitaElAccesoDeEscritura() throws Exception {
		// CAJERO puede registrar ventas porque tiene el modulo Ventas en rol_modulo
		permisosService.limpiar();
		mockMvc.perform(post("/api/minimarket/ventas").with(como("CAJERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());

		// se le retira el modulo en la tabla...
		rolModuloRepository.findAll().stream()
				.filter(rm -> "CAJERO".equals(rm.getRol().getNombre()) && "Ventas".equals(rm.getModulo().getNombre()))
				.forEach(rolModuloRepository::delete);
		rolModuloRepository.flush();
		permisosService.limpiar();

		// ...y el backend le niega la escritura sin tocar codigo
		mockMvc.perform(post("/api/minimarket/ventas").with(como("CAJERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
		permisosService.limpiar();
	}

	@Test
	void superadminNoTieneSucursalPeroElUsuarioEmpresaSi() throws Exception {
		String alez = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"alez\",\"password\":\"1234\"}"))
				.andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertTrue(alez.contains("\"sucursal\":null"), alez);

		String empresa = mockMvc.perform(post("/api/minimarket/login").contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"empresa\",\"password\":\"1234\"}"))
				.andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertFalse(empresa.contains("\"sucursal\":null"), empresa);
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void superadminPuedeCrearOtroSuperadminSinSucursal() throws Exception {
		Integer rolSuperadmin = rolRepository.findByNombre("SUPERADMIN").get().getId();
		mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("alez", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"socio\",\"password\":\"abc123\",\"nombre\":\"Socio\",\"rolId\":" + rolSuperadmin + "}"))
				.andExpect(status().isOk());
	}

	@Test
	void usuarioEmpresaNoPuedeCrearUnSuperadmin() throws Exception {
		Integer rolSuperadmin = rolRepository.findByNombre("SUPERADMIN").get().getId();
		mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"intruso\",\"password\":\"x\",\"rolId\":" + rolSuperadmin + "}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void usuarioNoSuperadminDebeTenerSucursal() throws Exception {
		Integer rolCajero = rolRepository.findByNombre("CAJERO").get().getId();
		mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"cajero1\",\"password\":\"x\",\"rolId\":" + rolCajero + "}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void editarUnUsuarioSinEnviarPasswordConservaLaClave() throws Exception {
		Integer id = usuarioRepository.findByUsername("empresa").getIdUsuario();
		mockMvc.perform(put("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idUsuario\":" + id + ",\"nombre\":\"Nombre nuevo\"}"))
				.andExpect(status().isOk());
		// sigue pudiendo iniciar sesion con la misma clave
		tokenDe("empresa", "1234");
	}

	@Test
	void usuarioEmpresaNoPuedeModificarAlSuperadmin() throws Exception {
		Integer idAlez = usuarioRepository.findByUsername("alez").getIdUsuario();
		mockMvc.perform(put("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idUsuario\":" + idAlez + ",\"password\":\"hackeada\"}"))
				.andExpect(status().isForbidden());
		tokenDe("alez", "1234");
	}

	@Test
	void usuarioEmpresaExportaElArchivoDelHostEnEbcdic() throws Exception {
		byte[] archivo = mockMvc.perform(get("/api/minimarket/host/ventas/exportar?fecha=2026-10-17")
				.header("Authorization", "Bearer " + tokenDe("empresa", "1234")))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header()
						.string("Content-Disposition", "attachment; filename=\"VENTAS.20261017.EBC\""))
				.andReturn().getResponse().getContentAsByteArray();

		// sin ventas ese dia: cabecera (H) + totales (T) = 2 registros de 60 bytes
		org.junit.jupiter.api.Assertions.assertEquals(120, archivo.length);
		org.junit.jupiter.api.Assertions.assertEquals((byte) 0xC8, archivo[0]);
		org.junit.jupiter.api.Assertions.assertEquals((byte) 0xE3, archivo[60]);
	}

	@Test
	void elHostValidaUnArchivoRecibidoYRechazaUnoDanado() throws Exception {
		String token = tokenDe("empresa", "1234");
		byte[] valido = mockMvc.perform(get("/api/minimarket/host/ventas/exportar?fecha=2026-10-17")
				.header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsByteArray();

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
				.multipart("/api/minimarket/host/ventas/validar")
				.file(new org.springframework.mock.web.MockMultipartFile("archivo", "VENTAS.EBC", "application/octet-stream", valido))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.valido").value(true))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.cantidad").value(0));

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
				.multipart("/api/minimarket/host/ventas/validar")
				.file(new org.springframework.mock.web.MockMultipartFile("archivo", "MALO.EBC", "application/octet-stream", new byte[77]))
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.valido").value(false));
	}

	@Test
	void superadminNoAccedeAlIntercambioConElHost() throws Exception {
		mockMvc.perform(get("/api/minimarket/host/ventas/exportar?fecha=2026-10-17")
				.header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isForbidden());
	}

	private Integer idEmpresaDemo() {
		return usuarioRepository.findByUsername("empresa").getSucursal().getEmpresa().getIdempresa();
	}

	@Test
	void getEmpresasDevuelveElResumenDeSuscripcion() throws Exception {
		mockMvc.perform(get("/api/minimarket/empresas").header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isOk())
				// campos de siempre (compatibilidad con las pantallas existentes)
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].razonsocial").value("GADBACORP"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].ruc").value("20000000001"))
				// columnas del panel
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].cantidadSucursales").value(1))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].propietario.nombre").value("Propietario Demo"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].ubicacion.ciudad").value("Tarapoto"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].plan.nombre").value("Empresarial"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].plan.ciclo").value("MENSUAL"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].ingresoMensual").value(349.0))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].usuarios.limite").value(25))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].estadoSuscripcion.codigo").value("ACTIVO"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].vencimiento").exists());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void suspenderYReactivarCambianElEstadoYElIngreso() throws Exception {
		String token = tokenDe("alez", "1234");
		Integer id = idEmpresaDemo();

		mockMvc.perform(post("/api/minimarket/empresas/" + id + "/suspender").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estadoSuscripcion.codigo").value("SUSPENDIDO"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ingresoMensual").value(0));

		mockMvc.perform(post("/api/minimarket/empresas/" + id + "/reactivar").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estadoSuscripcion.codigo").value("ACTIVO"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ingresoMensual").value(349.0));
	}

	private Integer idPlan(String planesJson, String nombre) {
		java.util.regex.Matcher m = java.util.regex.Pattern
				.compile("\"idPlan\":(\\d+),\"nombre\":\"" + nombre + "\"").matcher(planesJson);
		org.junit.jupiter.api.Assertions.assertTrue(m.find(), "no existe el plan " + nombre + " en " + planesJson);
		return Integer.valueOf(m.group(1));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void cambiarElPlanActualizaLosLimitesYElIngreso() throws Exception {
		String token = tokenDe("alez", "1234");
		Integer id = idEmpresaDemo();
		String planes = mockMvc.perform(get("/api/minimarket/planes").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		Integer idBasico = idPlan(planes, "B\u00e1sico");

		mockMvc.perform(put("/api/minimarket/empresas/" + id + "/suscripcion").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idPlan\":" + idBasico + ",\"ciclo\":\"anual\",\"enPrueba\":true,\"fechaVencimiento\":\"2999-01-01\"}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.plan.nombre").value("B\u00e1sico"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.plan.ciclo").value("ANUAL"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.usuarios.limite").value(3))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estadoSuscripcion.codigo").value("PRUEBA"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ingresoMensual").value(0));

		mockMvc.perform(put("/api/minimarket/empresas/" + id + "/suscripcion").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content("{\"ciclo\":\"trimestral\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elPlanLimitaLaCantidadDeUsuariosDeLaEmpresa() throws Exception {
		String tokenAlez = tokenDe("alez", "1234");
		Integer id = idEmpresaDemo();
		String planes = mockMvc.perform(get("/api/minimarket/planes").header("Authorization", "Bearer " + tokenAlez))
				.andReturn().getResponse().getContentAsString();
		mockMvc.perform(put("/api/minimarket/empresas/" + id + "/suscripcion").header("Authorization", "Bearer " + tokenAlez)
				.contentType(MediaType.APPLICATION_JSON).content("{\"idPlan\":" + idPlan(planes, "Gratis") + "}"))
				.andExpect(status().isOk());

		// el plan Gratis permite 1 usuario y la empresa ya tiene uno
		Integer rolCajero = rolRepository.findByNombre("CAJERO").get().getId();
		Integer idSucursal = usuarioRepository.findByUsername("empresa").getSucursal().getIdSucursal();
		mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"cajero9\",\"password\":\"x\",\"rolId\":" + rolCajero + ",\"idSucursal\":" + idSucursal + "}"))
				.andExpect(status().isConflict())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("limite de usuarios")));
	}

	@Test
	void soloElSuperadminAdministraEmpresasYPlanes() throws Exception {
		String empresa = tokenDe("empresa", "1234");
		mockMvc.perform(post("/api/minimarket/empresas/1/suspender").header("Authorization", "Bearer " + empresa))
				.andExpect(status().isForbidden());
		mockMvc.perform(post("/api/minimarket/planes").header("Authorization", "Bearer " + empresa)
				.contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"X\",\"precioMensual\":1}"))
				.andExpect(status().isForbidden());
		mockMvc.perform(put("/api/minimarket/empresas/1/suscripcion").header("Authorization", "Bearer " + empresa)
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void verificarSunatSinTokenConfiguradoDevuelve400() throws Exception {
		Integer id = empresasRepository.findAll().get(0).getIdempresa();
		mockMvc.perform(post("/api/minimarket/empresas/" + id + "/verificar-sunat")
				.header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isBadRequest());
	}

	private static final String FORMULARIO = "{\"nombreComercial\":\"Minimarket San Borja Express\",\"ruc\":\"20601928491\","
			+ "\"razonsocial\":\"\",\"propietario\":\"Juan Carlos P\u00e9rez\",\"correo\":\"contacto@empresa.pe\","
			+ "\"telefono\":\"+51 987 654 321\",\"direccion\":\"Av. Javier Prado Este 2400\",\"distrito\":\"San Borja\","
			+ "\"ciudad\":\"Lima\",\"departamento\":\"Lima\",\"sucursales\":2,\"ciclo\":\"MENSUAL\","
			+ "\"estadoInicial\":\"PRUEBA\",\"facturacionElectronica\":true,\"notas\":\"Pago adelantado\"}";

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elSuperadminRegistraUnaEmpresaConElFormularioCompleto() throws Exception {
		String token = tokenDe("alez", "1234");
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(FORMULARIO))
				.andExpect(status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nombreComercial").value("Minimarket San Borja Express"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.razonsocial").value("Minimarket San Borja Express"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ruc").value("20601928491"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.propietario.nombre").value("Juan Carlos P\u00e9rez"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.propietario.telefono").value("+51 987 654 321"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.correo").value("contacto@empresa.pe"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ubicacion.distrito").value("San Borja"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ubicacion.departamento").value("Lima"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.cantidadSucursales").value(2))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.facturacionElectronica").value(true))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.notas").value("Pago adelantado"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estadoSuscripcion.codigo").value("PRUEBA"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estadoSuscripcion.diasRestantes").value(14))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ingresoMensual").value(0))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.usuarios.usados").value(0));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elAltaCreaLasSucursalesYApareceEnElListado() throws Exception {
		String token = tokenDe("alez", "1234");
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(FORMULARIO)).andExpect(status().isCreated());

		String listado = mockMvc.perform(get("/api/minimarket/empresas").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertTrue(listado.contains("Minimarket San Borja Express"), listado);
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void unRucRepetidoDevuelve409() throws Exception {
		String token = tokenDe("alez", "1234");
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(FORMULARIO)).andExpect(status().isCreated());
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(FORMULARIO))
				.andExpect(status().isConflict())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("20601928491")));
	}

	@Test
	void losErroresDelFormularioLlegaranPorCampo() throws Exception {
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + tokenDe("alez", "1234"))
				.contentType(MediaType.APPLICATION_JSON).content("{\"ruc\":\"123\",\"correo\":\"mal\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Datos inv\u00e1lidos"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.nombreComercial").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.ruc").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.correo").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.telefono").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.direccion").exists());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elPlanLimitaLasSucursalesPedidasEnElAlta() throws Exception {
		String token = tokenDe("alez", "1234");
		String planes = mockMvc.perform(get("/api/minimarket/planes").header("Authorization", "Bearer " + token))
				.andReturn().getResponse().getContentAsString();
		Integer idBasico = idPlan(planes, "B\u00e1sico"); // permite 1 sucursal

		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(FORMULARIO.replace("\"sucursales\":2,", "\"sucursales\":2,\"idPlan\":" + idBasico + ",")))
				.andExpect(status().isConflict())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("sucursal")));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void laEdicionPorJsonSoloCambiaLoQueSeEnvia() throws Exception {
		String token = tokenDe("alez", "1234");
		Integer id = idEmpresaDemo();
		mockMvc.perform(put("/api/minimarket/empresas/" + id).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content("{\"notas\":\"cliente VIP\",\"distrito\":\"Morales\"}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.notas").value("cliente VIP"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ubicacion.distrito").value("Morales"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.razonsocial").value("GADBACORP"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.plan.nombre").value("Empresarial"));
	}

	@Test
	void laEmpresaNoPuedeRegistrarOtraEmpresa() throws Exception {
		mockMvc.perform(post("/api/minimarket/empresas").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON).content(FORMULARIO))
				.andExpect(status().isForbidden());
	}

	@Test
	void elPlanProfesionalCuestaLoQueMuestraLaPantalla() throws Exception {
		String planes = mockMvc.perform(get("/api/minimarket/planes").header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertTrue(
				planes.matches("(?s).*\"nombre\":\"Profesional\",\"precioMensual\":169(\\.0+)?,.*"), planes);
	}

	@Test
	void superadminListaTodosLosUsuariosSinVerLasClaves() throws Exception {
		String json = mockMvc.perform(get("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"username\":\"alez\"") && json.contains("\"username\":\"empresa\""), json);
		org.junit.jupiter.api.Assertions.assertFalse(json.contains("\"password\""), "el hash no debe salir en el JSON");
	}

	@Test
	void superadminListaLosUsuariosDeUnaEmpresa() throws Exception {
		mockMvc.perform(get("/api/minimarket/empresas/" + empresasRepository.findAll().get(0).getIdempresa() + "/usuarios")
				.header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].username").value("empresa"));
		mockMvc.perform(get("/api/minimarket/empresas/9999/usuarios").header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isNotFound());
	}

	@Test
	void unUsuarioDeEmpresaNoListaLosUsuariosDeOtraEmpresa() throws Exception {
		mockMvc.perform(get("/api/minimarket/empresas/" + empresasRepository.findAll().get(0).getIdempresa() + "/usuarios")
				.header("Authorization", "Bearer " + tokenDe("empresa", "1234")))
				.andExpect(status().isForbidden());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void superadminCreaAlPropietarioIndicandoSoloLaEmpresa() throws Exception {
		Integer idEmpresa = empresasRepository.findAll().get(0).getIdempresa();
		Integer rolEmpresa = rolRepository.findByNombre("EMPRESA").get().getId();

		mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("alez", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"dueno1\",\"password\":\"clave123\",\"nombre\":\"Due\u00f1o\",\"rolId\":" + rolEmpresa
						+ ",\"idEmpresa\":" + idEmpresa + "}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.username").value("dueno1"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sucursal.idSucursal").exists());

		// y el nuevo propietario ya puede iniciar sesion
		tokenDe("dueno1", "clave123");
	}

	@Test
	void superadminNoPuedeCrearUnUsuarioSinSucursalNiEmpresa() throws Exception {
		Integer rolEmpresa = rolRepository.findByNombre("EMPRESA").get().getId();
		mockMvc.perform(post("/api/minimarket/usuarios").header("Authorization", "Bearer " + tokenDe("alez", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"huerfano\",\"password\":\"x\",\"rolId\":" + rolEmpresa + "}"))
				.andExpect(status().isBadRequest());
	}

	@Autowired
	private com.gadbacorp.api.repository.seguridad.ModuloRepository moduloRepository;

	private Integer idModulo(String nombre) {
		return moduloRepository.findAll().stream().filter(m -> nombre.equals(m.getNombre()))
				.findFirst().orElseThrow().getIdModulo();
	}

	private String bearerAlez() throws Exception {
		return "Bearer " + tokenDe("alez", "1234");
	}

	@Test
	void listarRolesIncluyeModulosUsuariosYSiEsDelSistema() throws Exception {
		mockMvc.perform(get("/api/minimarket/roles").header("Authorization", bearerAlez()))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(6))
				// campos de siempre
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='SUPERADMIN')].id").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='SUPERADMIN')].estado").value(1))
				// nuevos
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='SUPERADMIN')].sistema").value(true))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='SUPERADMIN')].cantidadUsuarios").value(1))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='SUPERADMIN')].modulos[*].nombre").value(
						org.hamcrest.Matchers.containsInAnyOrder("Administrable", "Seguridad")))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='EMPRESA')].modulos.length()").value(10));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void crearUnRolConSusModulosEnUnSoloPaso() throws Exception {
		String respuesta = mockMvc.perform(post("/api/minimarket/roles").header("Authorization", bearerAlez())
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"Jefe de tienda\",\"modulos\":[" + idModulo("Ventas") + "," + idModulo("Caja") + "]}"))
				.andExpect(status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nombre").value("JEFE_DE_TIENDA"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.sistema").value(false))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.cantidadUsuarios").value(0))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.modulos.length()").value(2))
				.andReturn().getResponse().getContentAsString();

		// el permiso ya rige: el rol nuevo puede escribir en Ventas y Caja, no en Productos
		org.junit.jupiter.api.Assertions.assertTrue(permisosService.rolTieneModulo("JEFE_DE_TIENDA", "Ventas"), respuesta);
		org.junit.jupiter.api.Assertions.assertFalse(permisosService.rolTieneModulo("JEFE_DE_TIENDA", "Productos"));
		mockMvc.perform(post("/api/minimarket/ventas").with(como("JEFE_DE_TIENDA"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(put("/api/minimarket/productos").with(como("JEFE_DE_TIENDA"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void guardarLaMatrizReemplazaLosModulosDelRol() throws Exception {
		Integer id = rolRepository.save(rolNuevo("AUDITOR")).getId();
		String bearer = bearerAlez();

		mockMvc.perform(put("/api/minimarket/roles/" + id + "/modulos").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"modulos\":[" + idModulo("Reportes") + "," + idModulo("Dashboard") + "]}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.modulos[*].nombre").value(org.hamcrest.Matchers.containsInAnyOrder("Reportes", "Dashboard")));

		// reemplazo: queda solo Caja; guardar dos veces lo mismo no duplica
		for (int i = 0; i < 2; i++) {
			mockMvc.perform(put("/api/minimarket/roles/" + id + "/modulos").header("Authorization", bearer)
					.contentType(MediaType.APPLICATION_JSON).content("{\"modulos\":[" + idModulo("Caja") + "]}"))
					.andExpect(status().isOk())
					.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.modulos.length()").value(1))
					.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.modulos[0].nombre").value("Caja"));
		}
		org.junit.jupiter.api.Assertions.assertFalse(permisosService.rolTieneModulo("AUDITOR", "Reportes"));
		org.junit.jupiter.api.Assertions.assertTrue(permisosService.rolTieneModulo("AUDITOR", "Caja"));

		mockMvc.perform(put("/api/minimarket/roles/" + id + "/modulos").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"modulos\":[]}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.modulos.length()").value(0));
	}

	private com.gadbacorp.api.entity.seguridad.Rol rolNuevo(String nombre) {
		com.gadbacorp.api.entity.seguridad.Rol r = new com.gadbacorp.api.entity.seguridad.Rol();
		r.setNombre(nombre);
		r.setEstado(1);
		return r;
	}

	@Test
	void validacionesAlCrearUnRol() throws Exception {
		String bearer = bearerAlez();
		mockMvc.perform(post("/api/minimarket/roles").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"  \"}"))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.nombre").exists());
		mockMvc.perform(post("/api/minimarket/roles").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"ab\"}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/api/minimarket/roles").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"cajero\"}"))
				.andExpect(status().isConflict())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("CAJERO")));
		mockMvc.perform(post("/api/minimarket/roles").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Nuevo\",\"modulos\":[999999]}"))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.modulos").exists());
	}

	@Test
	void losRolesDelSistemaNoSeRenombranNiSeEliminan() throws Exception {
		String bearer = bearerAlez();
		Integer idCajero = rolRepository.findByNombre("CAJERO").get().getId();
		mockMvc.perform(put("/api/minimarket/roles").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"id\":" + idCajero + ",\"nombre\":\"OTRO\"}"))
				.andExpect(status().isConflict())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("sistema")));
		mockMvc.perform(delete("/api/minimarket/roles/" + idCajero).header("Authorization", bearer))
				.andExpect(status().isConflict());
	}

	@Test
	void elSuperadminSoloPuedeTenerAdministrableYSeguridad() throws Exception {
		String bearer = bearerAlez();
		Integer idSuper = rolRepository.findByNombre("SUPERADMIN").get().getId();
		mockMvc.perform(put("/api/minimarket/roles/" + idSuper + "/modulos").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"modulos\":[" + idModulo("Administrable") + "," + idModulo("Ventas") + "]}"))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.modulos").exists());
		mockMvc.perform(put("/api/minimarket/roles/" + idSuper + "/modulos").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content("{\"modulos\":[" + idModulo("Seguridad") + "]}"))
				.andExpect(status().isBadRequest()); // sin Administrable
	}

	@Test
	void noSeEliminaUnRolQueTieneUsuarios() throws Exception {
		Integer idEmpresaRol = rolRepository.findByNombre("EMPRESA").get().getId();
		mockMvc.perform(delete("/api/minimarket/roles/" + idEmpresaRol).header("Authorization", bearerAlez()))
				.andExpect(status().isConflict());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void seEliminaUnRolPropioSinUsuariosYSeLimpianSusPermisos() throws Exception {
		String bearer = bearerAlez();
		String creado = mockMvc.perform(post("/api/minimarket/roles").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"nombre\":\"TEMPORAL\",\"modulos\":[" + idModulo("Ventas") + "]}"))
				.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		Integer id = Integer.valueOf(creado.replaceAll("(?s).*?\"id\":(\\d+).*", "$1"));
		org.junit.jupiter.api.Assertions.assertTrue(permisosService.rolTieneModulo("TEMPORAL", "Ventas"));

		mockMvc.perform(delete("/api/minimarket/roles/" + id).header("Authorization", bearer))
				.andExpect(status().isNoContent());
		mockMvc.perform(get("/api/minimarket/roles/" + id).header("Authorization", bearer))
				.andExpect(status().isNotFound());
		org.junit.jupiter.api.Assertions.assertFalse(permisosService.rolTieneModulo("TEMPORAL", "Ventas"));
	}

	@Test
	void soloElSuperadminAdministraRoles() throws Exception {
		String empresa = "Bearer " + tokenDe("empresa", "1234");
		mockMvc.perform(post("/api/minimarket/roles").header("Authorization", empresa)
				.contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"INTRUSO\"}"))
				.andExpect(status().isForbidden());
		mockMvc.perform(put("/api/minimarket/roles/1/modulos").header("Authorization", empresa)
				.contentType(MediaType.APPLICATION_JSON).content("{\"modulos\":[]}"))
				.andExpect(status().isForbidden());
		// pero cualquier usuario autenticado puede listarlos (los formularios de usuarios lo necesitan)
		mockMvc.perform(get("/api/minimarket/roles").header("Authorization", empresa)).andExpect(status().isOk());
	}

	@Test
	void rolesAsignablesOcultanSuperadminALosDemas() throws Exception {
		mockMvc.perform(get("/api/minimarket/roles/asignables").header("Authorization", "Bearer " + tokenDe("empresa", "1234")))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(5))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[?(@.nombre=='SUPERADMIN')]").isEmpty());
		mockMvc.perform(get("/api/minimarket/roles/asignables").header("Authorization", bearerAlez()))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(6));
	}

	@Test
	void losModulosDelSistemaNoSeEliminanNiSeRenombran() throws Exception {
		String bearer = bearerAlez();
		mockMvc.perform(delete("/api/minimarket/modulos/" + idModulo("Ventas")).header("Authorization", bearer))
				.andExpect(status().isConflict());
		mockMvc.perform(put("/api/minimarket/modulos").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idModulo\":" + idModulo("Ventas") + ",\"nombre\":\"Otro\"}"))
				.andExpect(status().isConflict());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elEndpointAntiguoDePermisosYaNoPermiteDuplicados() throws Exception {
		String bearer = bearerAlez();
		Integer idCajero = rolRepository.findByNombre("CAJERO").get().getId();
		mockMvc.perform(post("/api/minimarket/rolesmodulos").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"rol\":{\"id\":" + idCajero + "},\"modulo\":{\"idModulo\":" + idModulo("Ventas") + "}}"))
				.andExpect(status().isConflict());
	}

	private static String proveedorJson(Integer idEmpresa, String ruc) {
		return "{\"nombre\":\"Distribuidora Andina SAC\",\"ruc\":\"" + ruc + "\",\"regimen\":\"General\","
				+ "\"telefono\":\"987654321\",\"gmail\":\"ventas@andina.pe\",\"direccion\":\"Av. Industrial 123\","
				+ "\"idEmpresa\":" + idEmpresa + "}";
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void crearProveedorDevuelve201ConLosDatosCalculadosPorElServidor() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andExpect(status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.idProveedor").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nombre").value("Distribuidora Andina SAC"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estado").value(1))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.fecha_registro").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.idEmpresa").value(idEmpresa))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.empresaNombre").value("GADBACORP"));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elProveedorCreadoApareceEnLaListaDeSuEmpresa() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		mockMvc.perform(get("/api/minimarket/proveedores/empresas/" + idEmpresa).header("Authorization", bearer))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(0));   // lista vacia: 200 con [] (antes 204)

		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/minimarket/proveedores/empresas/" + idEmpresa).header("Authorization", bearer))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.length()").value(1))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].ruc").value("20601928491"));
	}

	@Test
	void losErroresDeValidacionLleganPorCampo() throws Exception {
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON).content("{\"ruc\":\"123\",\"gmail\":\"mal\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value("Datos inv\u00e1lidos"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.nombre").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.ruc").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.gmail").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.idEmpresa").exists());
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elRucRepetidoEnLaMismaEmpresaDevuelve409() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andExpect(status().isCreated());
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andExpect(status().isConflict())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("20601928491")));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void editarConservaLaFechaDeRegistroYEliminarLoOcultaDeLaLista() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		String creado = mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andReturn().getResponse().getContentAsString();
		Integer id = Integer.valueOf(creado.replaceAll("(?s).*\"idProveedor\":(\\d+).*", "$1"));
		String fecha = creado.replaceAll("(?s).*\"fecha_registro\":\"([^\"]+)\".*", "$1");

		mockMvc.perform(put("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"idProveedor\":" + id + ",\"nombre\":\"Nombre nuevo\",\"ruc\":\"20601928491\",\"idEmpresa\":" + idEmpresa + "}"))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nombre").value("Nombre nuevo"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.fecha_registro").value(fecha))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.estado").value(1));

		mockMvc.perform(delete("/api/minimarket/proveedores/" + id).header("Authorization", bearer))
				.andExpect(status().isOk());
		mockMvc.perform(get("/api/minimarket/proveedores/" + id).header("Authorization", bearer))
				.andExpect(status().isNotFound());
	}

	@Test
	void editarOEliminarUnProveedorInexistenteDevuelve404() throws Exception {
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		mockMvc.perform(put("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresaDemo(), "20601928491").replace("{", "{\"idProveedor\":9999,")))
				.andExpect(status().isNotFound());
		mockMvc.perform(delete("/api/minimarket/proveedores/9999").header("Authorization", bearer))
				.andExpect(status().isNotFound());
	}

	@Test
	void permisosDelModuloComprasEnProveedores() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		// ALMACENERO tiene el modulo Compras: llega a la validacion (400 por cuerpo vacio)
		mockMvc.perform(post("/api/minimarket/proveedores").with(como("ALMACENERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
		// CAJERO no lo tiene: solo puede consultar
		mockMvc.perform(post("/api/minimarket/proveedores").with(como("CAJERO"))
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/api/minimarket/proveedores").with(como("CAJERO"))).andExpect(status().isOk());
		// SUPERADMIN no ve datos operativos
		mockMvc.perform(get("/api/minimarket/proveedores").header("Authorization", "Bearer " + tokenDe("alez", "1234")))
				.andExpect(status().isForbidden());
	}

	@Test
	void consultaSunatRechazaUnRucMalFormadoSinLlamarAInternet() throws Exception {
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		for (String ruc : new String[] {"123", "abcdefghijk", "2060192849"}) {
			mockMvc.perform(get("/api/minimarket/proveedores/sunat/" + ruc).header("Authorization", bearer))
					.andExpect(status().isBadRequest())
					.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message")
							.value(org.hamcrest.Matchers.containsString("11")));
		}
		// y exige sesion: sin token es 401
		mockMvc.perform(get("/api/minimarket/proveedores/sunat/20601928491")).andExpect(status().isUnauthorized());
	}

	@Test
	void sinTokenVencidoOAlteradoLaRespuestaEs401() throws Exception {
		String vencido = new com.gadbacorp.api.config.JwtUtil("test-secret-test-secret-test-secret-0123456789", -60_000).generarToken("ale");
		String ajeno = new com.gadbacorp.api.config.JwtUtil("otra-clave-otra-clave-otra-clave-0123456789", 60_000).generarToken("ale");
		String cuerpo = "{\"nombre\":\"X\"}";

		mockMvc.perform(post("/api/minimarket/proveedores").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
				.andExpect(status().isUnauthorized())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.error").exists());
		for (String token : new String[] {vencido, ajeno, "no-es-un-token"}) {
			mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", "Bearer " + token)
					.contentType(MediaType.APPLICATION_JSON).content(cuerpo))
					.andExpect(status().isUnauthorized());
			mockMvc.perform(get("/api/minimarket/proveedores").header("Authorization", "Bearer " + token))
					.andExpect(status().isUnauthorized());
		}
	}

	@Test
	void conSesionValidaPeroSinPermisoSigueSiendo403() throws Exception {
		// CAJERO esta autenticado pero no tiene el modulo Compras: 403, no 401
		mockMvc.perform(post("/api/minimarket/proveedores").with(como("CAJERO"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isForbidden());
	}

	@Test
	void unAdminConSesionValidaPuedeEscribirProveedores() throws Exception {
		// el rol ADMIN tiene el modulo Compras sembrado: la autorizacion pasa y falla solo por validacion (400)
		mockMvc.perform(post("/api/minimarket/proveedores").with(como("ADMIN"))
				.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}

	private static final String PROVEEDOR_COMPLETO = "{\"nombre\":\"Andina\",\"razonSocial\":\"DISTRIBUIDORA ANDINA S.A.C.\","
			+ "\"ruc\":\"20601928491\",\"regimen\":\"General\",\"telefono\":\"987654321\",\"gmail\":\"ventas@andina.pe\","
			+ "\"direccion\":\"Av. Industrial 123\",\"distrito\":\"San Borja\",\"provincia\":\"Lima\",\"departamento\":\"Lima\","
			+ "\"contactoNombre\":\"Mar\u00eda Quispe\",\"contactoCargo\":\"Ventas\",\"telefonoSecundario\":\"912345678\","
			+ "\"sitioWeb\":\"www.andina.pe\",\"rubro\":\"Abarrotes\",\"condicionPago\":\"CREDITO\",\"diasCredito\":30,"
			+ "\"moneda\":\"PEN\",\"limiteCredito\":5000.50,\"banco\":\"BCP\",\"numeroCuenta\":\"193-1234567-0-12\","
			+ "\"cci\":\"00219300123456701234\",\"estadoSunat\":\"ACTIVO\",\"condicionSunat\":\"HABIDO\",\"notas\":\"Entrega los martes\"";

	@Test
	@org.springframework.transaction.annotation.Transactional
	void laFichaCompletaViajaEnElCreateYVuelveEnLaLista() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(PROVEEDOR_COMPLETO + ",\"idEmpresa\":" + idEmpresa + "}"))
				.andExpect(status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.razonSocial").value("DISTRIBUIDORA ANDINA S.A.C."))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.distrito").value("San Borja"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.contactoNombre").value("Mar\u00eda Quispe"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.condicionPago").value("CREDITO"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.diasCredito").value(30))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.limiteCredito").value(5000.5))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.cci").value("00219300123456701234"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.condicionSunat").value("HABIDO"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.tipoPersona").value("JURIDICA"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalCompras").value(0))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.montoTotalComprado").value(0))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ultimaCompra").doesNotExist());

		mockMvc.perform(get("/api/minimarket/proveedores/empresas/" + idEmpresa).header("Authorization", bearer))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].rubro").value("Abarrotes"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].banco").value("BCP"));
	}

	@Test
	@org.springframework.transaction.annotation.Transactional
	void unProveedorSimpleSigueFuncionandoYTomaLosValoresPorDefecto() throws Exception {
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresaDemo(), "10456789012")))
				.andExpect(status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.condicionPago").value("CONTADO"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.moneda").value("PEN"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.tipoPersona").value("NATURAL"));
	}

	@Test
	void losErroresDeLaFichaLleganPorCampo() throws Exception {
		mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", "Bearer " + tokenDe("empresa", "1234"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(proveedorJson(idEmpresaDemo(), "20601928491").replace("}", ",\"cci\":\"123\",\"condicionPago\":\"CREDITO\"}")))
				.andExpect(status().isBadRequest())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.cci").exists())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.errors.diasCredito").exists());
	}

	@Autowired
	private com.gadbacorp.api.repository.compras.ComprasRepository comprasRepository;

	@Test
	@org.springframework.transaction.annotation.Transactional
	void elProveedorMuestraSuHistorialDeCompras() throws Exception {
		Integer idEmpresa = idEmpresaDemo();
		String bearer = "Bearer " + tokenDe("empresa", "1234");
		String creado = mockMvc.perform(post("/api/minimarket/proveedores").header("Authorization", bearer)
				.contentType(MediaType.APPLICATION_JSON).content(proveedorJson(idEmpresa, "20601928491")))
				.andReturn().getResponse().getContentAsString();
		Integer id = Integer.valueOf(creado.replaceAll("(?s).*\"idProveedor\":(\\d+).*", "$1"));

		com.gadbacorp.api.entity.compras.Proveedores proveedor = new com.gadbacorp.api.entity.compras.Proveedores();
		proveedor.setIdProveedor(id);
		for (String[] datos : new String[][] {{"100.50", "2026-10-01T10:00:00"}, {"250.00", "2026-10-20T15:30:00"}}) {
			com.gadbacorp.api.entity.compras.Compras compra = new com.gadbacorp.api.entity.compras.Compras();
			compra.setProveedor(proveedor);
			compra.setTotal(new java.math.BigDecimal(datos[0]));
			compra.setFechaCompra(java.time.LocalDateTime.parse(datos[1]));
			compra.setEstado(1);
			comprasRepository.save(compra);
		}
		comprasRepository.flush();

		mockMvc.perform(get("/api/minimarket/proveedores/" + id).header("Authorization", bearer))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.totalCompras").value(2))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.montoTotalComprado").value(350.5))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.ultimaCompra").value("2026-10-20"));
	}
}
