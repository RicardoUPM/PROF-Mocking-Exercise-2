package es.grise.upm.profundizacion.mocking.exercise2;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class EngineControllerTest {
    private Logger logger;
    private Speedometer speedometer;
    private Gearbox gearbox;
    private Time time;
    private EngineController engineController;

    @BeforeEach
    public void setup() {
        logger = mock(Logger.class);
        speedometer = mock(Speedometer.class);
        gearbox = mock(Gearbox.class);
        time = mock(Time.class);
        engineController = new EngineController(logger, speedometer, gearbox, time);
    }

    @Test
    public void testRecordGearLogsCorrectFormat() {
        // Mock para devolver un timestamp específico
        when(time.getCurrentTime()).thenReturn(new Timestamp(0)); // 1970-01-01 00:00:00 UTC

        // Ejecuta el método
        engineController.recordGear(GearValues.FIRST);

        // Captura el log generado
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(logger).log(captor.capture());

        // Configurar el formato de la fecha en la zona horaria del sistema
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault()); // Usa la zona horaria del sistema (para sincronizar con EngineController)

        // Mensaje esperado ajustado a la zona horaria del sistema
        String expectedLog = sdf.format(new Timestamp(0)) + " Gear changed to FIRST";

        // Verificación
        assertEquals(expectedLog, captor.getValue());
    }

    @Test
    public void testGetInstantaneousSpeedCalculatesCorrectly() {
        // Configuración de valores simulados de velocidad
        when(speedometer.getSpeed()).thenReturn(10.0, 20.0, 30.0);

        // Ejecutar el método a probar
        double speed = engineController.getInstantaneousSpeed();

        // Verificar el promedio calculado
        assertEquals(20.0, speed, 0.01);
    }

    @Test
    public void testAdjustGearCallsGetInstantaneousSpeedThreeTimes() {
        // Configuración de valores simulados para evitar NullPointerException
        when(speedometer.getSpeed()).thenReturn(10.0);
        when(time.getCurrentTime()).thenReturn(new Timestamp(0)); // Simula un timestamp válido

        // Ejecuta el método a probar
        engineController.adjustGear();

        // Verifica que getSpeed() fue llamado exactamente tres veces
        verify(speedometer, times(3)).getSpeed();
    }

    @Test
    public void testAdjustGearLogsNewGear() {
        // Configuración de valores simulados
        when(speedometer.getSpeed()).thenReturn(10.0);
        when(time.getCurrentTime()).thenReturn(new Timestamp(0)); // Simula un timestamp válido

        // Ejecutar el método a probar
        engineController.adjustGear();

        // Capturar el mensaje de log y verifica su contenido
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(logger).log(captor.capture());
        assertTrue(captor.getValue().contains("Gear changed to FIRST"));
    }

    @Test
    public void testAdjustGearSetsCorrectGear() {
        // Configuración de valores simulados
        when(speedometer.getSpeed()).thenReturn(10.0);
        when(time.getCurrentTime()).thenReturn(new Timestamp(0)); // Simula un timestamp válido

        // Ejecutar el método a probar
        engineController.adjustGear();

        // Verificar que setGear() fue llamado con la marcha correcta
        verify(gearbox).setGear(GearValues.FIRST);
    }
}
