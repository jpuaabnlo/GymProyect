package com.example.proyectogym.ui.coach

data class CatalogExercise(
    val id: String,
    val nombre: String,
    val grupoMuscular: String,
    val equipo: String,
    val descripcion: String
)

val MUSCLE_GROUPS = listOf(
    "Pecho",
    "Espalda",
    "Pierna",
    "Hombro",
    "Brazo",
    "Abdomen",
    "Cuerpo completo"
)

val EXERCISE_CATALOG: List<CatalogExercise> = listOf(
    // Pecho
    CatalogExercise("pecho_1", "Press de banca plano", "Pecho", "Barra", "Empuje horizontal para pectoral medio"),
    CatalogExercise("pecho_2", "Press inclinado con mancuernas", "Pecho", "Mancuernas", "Enfoque en porción superior del pecho"),
    CatalogExercise("pecho_3", "Aperturas en polea o máquina", "Pecho", "Polea / Máquina", "Aislamiento con tensión continua"),
    CatalogExercise("pecho_4", "Fondos en paralelas", "Pecho", "Peso corporal", "Pectoral inferior y tríceps"),
    CatalogExercise("pecho_5", "Flexiones de pecho", "Pecho", "Peso corporal", "Empuje básico de control corporal"),

    // Espalda
    CatalogExercise("espalda_1", "Jalón al pecho en polea", "Espalda", "Polea", "Amplitud dorsal y control escapular"),
    CatalogExercise("espalda_2", "Remo con barra", "Espalda", "Barra", "Densidad de espalda media y dorsal"),
    CatalogExercise("espalda_3", "Remo con mancuerna a una mano", "Espalda", "Mancuerna", "Trabajo unilateral de dorsal ancho"),
    CatalogExercise("espalda_4", "Dominadas pronas", "Espalda", "Barra fija", "Tracción vertical con peso corporal"),
    CatalogExercise("espalda_5", "Remo sentado en polea baja", "Espalda", "Polea", "Retracción escapular y romboides"),

    // Pierna
    CatalogExercise("pierna_1", "Sentadilla libre con barra", "Pierna", "Barra", "Cuádriceps, glúteos y core"),
    CatalogExercise("pierna_2", "Prensa de piernas 45°", "Pierna", "Máquina", "Empuje guiado de tren inferior"),
    CatalogExercise("pierna_3", "Peso muerto rumano", "Pierna", "Barra / Mancuernas", "Enfoque en isquiotibiales y glúteo"),
    CatalogExercise("pierna_4", "Extensión de cuádriceps", "Pierna", "Máquina", "Aislamiento de cuádriceps"),
    CatalogExercise("pierna_5", "Curl femoral acostado", "Pierna", "Máquina", "Aislamiento de femorales"),
    CatalogExercise("pierna_6", "Elevación de talones de pie", "Pierna", "Máquina / Mancuerna", "Trabajo directo de pantorrillas"),

    // Hombro
    CatalogExercise("hombro_1", "Press militar con mancuernas", "Hombro", "Mancuernas", "Empuje vertical para deltoides"),
    CatalogExercise("hombro_2", "Elevaciones laterales", "Hombro", "Mancuernas", "Deltoides lateral y amplitud"),
    CatalogExercise("hombro_3", "Pájaros posteriores", "Hombro", "Mancuernas / Polea", "Deltoides posterior y postura"),
    CatalogExercise("hombro_4", "Face pull en polea", "Hombro", "Polea", "Salud escapular y deltoides posterior"),

    // Brazo
    CatalogExercise("brazo_1", "Curl de bíceps con barra", "Brazo", "Barra", "Fuerza y volumen de bíceps"),
    CatalogExercise("brazo_2", "Curl martillo con mancuernas", "Brazo", "Mancuernas", "Braquial y antebrazo"),
    CatalogExercise("brazo_3", "Extensión de tríceps en polea", "Brazo", "Polea", "Aislamiento de tríceps con cuerda o barra"),
    CatalogExercise("brazo_4", "Press francés con barra Z", "Brazo", "Barra Z", "Cabeza larga del tríceps"),

    // Abdomen
    CatalogExercise("abs_1", "Plancha abdominal isométrica", "Abdomen", "Peso corporal", "Estabilidad del core completo"),
    CatalogExercise("abs_2", "Crunch en polea alta", "Abdomen", "Polea", "Flexión abdominal con carga progresiva"),
    CatalogExercise("abs_3", "Elevación de piernas colgado", "Abdomen", "Barra fija", "Abdomen inferior y flexores"),
    CatalogExercise("abs_4", "Giros rusos con disco", "Abdomen", "Disco / Balón", "Oblicuos y rotación controlada"),

    // Cuerpo completo
    CatalogExercise("full_1", "Peso muerto convencional", "Cuerpo completo", "Barra", "Cadena posterior y fuerza global"),
    CatalogExercise("full_2", "Thrusters con mancuernas", "Cuerpo completo", "Mancuernas", "Sentadilla frontal más press vertical"),
    CatalogExercise("full_3", "Burpees", "Cuerpo completo", "Peso corporal", "Acondicionamiento metabólico"),
    CatalogExercise("full_4", "Paseo del granjero", "Cuerpo completo", "Mancuernas / Kettlebells", "Agarre, core y estabilidad")
)
