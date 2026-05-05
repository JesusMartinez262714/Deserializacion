package org.example;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

public class ProcesadorA08 {

    private BaseDeDatos db;
    private static final String CLAVE_SECRETA = "ClaveSecretaITSON2026";

    public ProcesadorA08(BaseDeDatos db) {
        this.db = db;
    }


    public String generarTokenVulnerable(String datos) {
        return Base64.getEncoder().encodeToString(datos.getBytes(StandardCharsets.UTF_8));
    }

    public void validarVulnerable(String tokenRecibido, Consumer<String> logger) {
        logger.accept("----------------------------------------");
        logger.accept("[INFO] Procesando token (MODO INSEGURO)...");
        try {
            byte[] bytesDecodificados = Base64.getDecoder().decode(tokenRecibido.trim());
            String datos = new String(bytesDecodificados, StandardCharsets.UTF_8);

            logger.accept("[ADVERTENCIA] Sistema confiando ciegamente en los datos recibidos.");
            ejecutarLogicaNegocio(datos, logger);

        } catch (Exception ex) {
            logger.accept("[ERROR] " + ex.getMessage());
        }
    }


    public String generarTokenSeguro(String datos) {
        String base64 = Base64.getEncoder().encodeToString(datos.getBytes(StandardCharsets.UTF_8));
        String firma = generarFirmaHMAC(datos);
        return base64 + "." + firma;
    }

    public void validarSeguro(String tokenRecibido, Consumer<String> logger) {
        logger.accept("----------------------------------------");
        logger.accept("[INFO] Procesando token (MODO SEGURO)...");
        try {
            String[] partes = tokenRecibido.trim().split("\\.");
            if (partes.length != 2) {
                logger.accept("[ERROR] Formato inválido. El token no tiene un sello de seguridad (HMAC).");
                return;
            }

            String datosBase64 = partes[0];
            String firmaRecibida = partes[1];

            byte[] bytesDecodificados = Base64.getDecoder().decode(datosBase64);
            String datos = new String(bytesDecodificados, StandardCharsets.UTF_8);

            logger.accept("[INFO] Verificando sello de seguridad HMAC-SHA256...");
            String firmaCalculada = generarFirmaHMAC(datos);

            if (!firmaCalculada.equals(firmaRecibida)) {
                logger.accept("[ALERTA CRÍTICA] ¡Sello de integridad ROTO!");
                logger.accept("-> Datos alterados en tránsito. Acceso denegado automáticamente.");
                return; // BLOQUEA AL HACKER AQUÍ
            }

            logger.accept("[ÉXITO] Firma válida. Integridad intacta.");
            ejecutarLogicaNegocio(datos, logger);

        } catch (Exception ex) {
            logger.accept("[ERROR] " + ex.getMessage());
        }
    }


    private void ejecutarLogicaNegocio(String datos, Consumer<String> logger) throws Exception {
        String usuario = extraerValor(datos, "usuario:");
        String rol = extraerValor(datos, "rol:");
        String comando = extraerValor(datos, "comando:");

        if (!db.existeUsuario(usuario)) {
            logger.accept("[ALERTA] Usuario '" + usuario + "' NO existe.");
            return;
        }

        if (!rol.equals("normal") && !rol.equals("admin")) {
            logger.accept("[ALERTA] Rol inválido: '" + rol + "'");
            return;
        }

        logger.accept("[SISTEMA] Identidad verificada -> Usuario: " + usuario + " | Rol: " + rol);

        if (!comando.isEmpty() && !comando.equals("ninguno")) {
            if (rol.equals("admin")) {
                logger.accept("[PELIGRO CRÍTICO] Ejecutando comando remoto: " + comando);
                MotorEjecucion.ejecutarComando(comando);
            } else {
                logger.accept("[BLOQUEADO] Permisos insuficientes para comandos del sistema.");
            }
        }
    }

    private String generarFirmaHMAC(String datos) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(CLAVE_SECRETA.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            return Base64.getEncoder().encodeToString(mac.doFinal(datos.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Error crítico al generar la firma de seguridad", e);
        }
    }

    private String extraerValor(String payload, String clave) {
        String[] partes = payload.split(";");
        for (String parte : partes) {
            if (parte.startsWith(clave)) {
                return parte.substring(clave.length()).trim();
            }
        }
        return "";
    }
}