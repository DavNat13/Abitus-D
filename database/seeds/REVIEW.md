# Revisión del catálogo de ejercicios

Archivo generado por `tools/import-exercises/build-review.mjs`. No editar a mano.

## Muestra para revisión humana (criterio de aceptación de la Fase 2)

Revisar en la app o en los archivos de `exercises/` que nombre, músculos, equipo e
indicaciones (es y en) sean correctos. Anotar errores en `REVIEW-NOTES.md` o en el chat.

- calisthenics: `push_up` (Flexión de brazos), `chin_up` (Dominada supina), `bench_dip` (Fondos en banco)
- core: `plank` (Plancha), `russian_twist` (Giro ruso), `jackknife_sit_up` (Abdominal en V)
- cycling: `road_cycling` (Ciclismo en ruta), `recumbent_bike` (Bicicleta reclinada), `hill_climb_ride` (Salida con subidas)
- functional: `farmers_walk` (Paseo del granjero), `kettlebell_snatch` (Arrancada con kettlebell), `tire_flip` (Volteo de neumático)
- hypertrophy: `dumbbell_bench_press` (Press de banca con mancuernas), `standing_overhead_press` (Press militar de pie), `dumbbell_triceps_kickback` (Patada de tríceps con mancuerna)
- mobility: `worlds_greatest_stretch` (El mejor estiramiento del mundo), `soleus_stretch` (Estiramiento de sóleo y tendón de Aquiles), `upper_back_stretch` (Estiramiento de espalda alta)
- plyometrics: `box_jump` (Salto al cajón), `standing_long_jump` (Salto de longitud sin carrera), `star_jump` (Salto de estrella)
- powerlifting: `barbell_back_squat` (Sentadilla trasera con barra), `pin_press` (Press desde soportes), `good_morning` (Buenos días)
- rowing: `rowing_machine` (Máquina de remo), `rowing_intervals` (Series de remo), `rowing_pick_drill` (Ejercicio técnico de remo por partes)
- running: `treadmill_run` (Carrera en cinta), `long_run` (Tirada larga), `hill_repeats` (Cuestas)
- swimming: `freestyle` (Crol), `freestyle_kick_board` (Patada de crol con tabla), `single_arm_freestyle` (Crol a un brazo)
- weightlifting: `snatch` (Arranque), `power_clean` (Cargada de potencia), `overhead_squat` (Sentadilla sobre la cabeza)

## Decisiones tomadas sobre datos dudosos de la fuente

- `mixed_grip_pull_up`: source listed rhomboids and middle trapezius as primary; lats set as primary like the other pull-ups
- `scapular_pull_up`: source listed upper trapezius; scapular depression is mainly lower trapezius and lats, changed
- `side_plank`: source muscles listed only abdominals and shoulders; obliques added as the main target of the side plank
- `russian_twist`: source listed abdominals as primary; obliques added as primary for a rotation exercise
- `ab_wheel_rollout`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `pallof_press`: source listed chest and triceps as secondary; for an anti-rotation drill obliques matter more. Muscles changed to abdominals and obliques
- `cable_wood_chop`: obliques added as primary for a rotation exercise
- `oblique_crunch`: obliques set as primary for a lateral crunch
- `mountain_climber`: source listed quadriceps as primary; abdominals set as primary since the plank hold is the main demand
- `dumbbell_side_bend`: obliques set as primary for a side bend
- `one_arm_kettlebell_swing`: source muscles kept: hamstrings primary; glutes are a main driver too
- `kettlebell_thruster`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `incline_dumbbell_fly`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `cable_crossover`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `hammer_curl`: forearm_flexors added as secondary (brachioradialis)
- `reverse_barbell_curl`: forearm_extensors set as secondary instead of flexors for the overhand grip
- `reverse_barbell_wrist_curl`: muscle changed to forearm_extensors (palms down wrist extension)
- `machine_hip_abduction`: abductors mapped to glutes (gluteus medius); no abductor group exists
- `butterfly_groin_stretch`: source exercise needed a partner or a stick; replaced by the common individual version with own text (original data: free-exercise-db Adductor_Groin (commit f00c92c))
- `it_band_glute_stretch`: source exercise needed a partner or a stick; replaced by the common individual version with own text (original data: free-exercise-db IT_Band_and_Glute_Stretch (commit f00c92c))
- `doorway_chest_stretch`: source exercise needed a partner or a stick; replaced by the common individual version with own text (original data: free-exercise-db Chest_And_Front_Of_Shoulder_Stretch (commit f00c92c))
- `overhead_triceps_stretch`: source exercise needed a partner or a stick; replaced by the common individual version with own text (original data: free-exercise-db Overhead_Triceps (commit f00c92c))
- `supine_glute_stretch`: source exercise needed a partner or a stick; replaced by the common individual version with own text (original data: free-exercise-db Lying_Glute (commit f00c92c))
- `lateral_box_jump`: abductors mapped to glutes (gluteus medius); no abductor group exists
- `lateral_bound`: abductors mapped to glutes (gluteus medius); no abductor group exists
- `alternate_leg_diagonal_bound`: abductors mapped to glutes (gluteus medius); no abductor group exists
- `single_leg_lateral_hop`: abductors mapped to glutes (gluteus medius); no abductor group exists
- `medicine_ball_slam`: source listed only lats; abdominals set as primary and lats, shoulders as secondary
- `backward_medicine_ball_throw`: source listed only shoulders; glutes and hamstrings set as primary for a hip-driven throw
- `snatch`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `power_snatch`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `hang_snatch`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `squat_clean`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `power_clean`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `hang_clean`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `overhead_squat`: shoulders: deltoid head not clear from the name; chose deltoid_anterior
- `snatch_balance`: shoulders: deltoid head not clear from the name; chose deltoid_anterior

## Ejercicios con texto propio (sin fuente externa)

- `cycling_intervals`, `hill_climb_ride`, `high_cadence_drill`, `butterfly_groin_stretch`, `it_band_glute_stretch`, `doorway_chest_stretch`, `overhead_triceps_stretch`, `supine_glute_stretch`
- `rowing_intervals`, `rowing_pick_drill`, `rowing_steady_state`, `easy_run`, `long_run`, `tempo_run`, `interval_run`, `fartlek`
- `hill_repeats`, `strides`, `a_skip`, `high_knees`, `butt_kicks`, `carioca`, `freestyle`, `backstroke`
- `breaststroke`, `butterfly`, `individual_medley`, `freestyle_kick_board`, `breaststroke_kick_board`, `pull_buoy_freestyle`, `catch_up_drill`, `fingertip_drag_drill`
- `single_arm_freestyle`, `side_kick_drill`, `sculling`, `streamline_dolphin_kick`, `open_water_swim`
