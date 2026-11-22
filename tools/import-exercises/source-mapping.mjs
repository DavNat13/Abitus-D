// Mapping from free-exercise-db fields to the Abitus-D catalog (08c, 08d).
// Anything that cannot be mapped with certainty is reported for human review.

export const SOURCE_COMMIT = 'f00c92c7dcf1216a928a52c3706c7ce8e2f71ed5';
export const SOURCE_LICENSE = 'Unlicense';

const DIRECT_MUSCLES = {
  abdominals: ['abdominals'],
  hamstrings: ['hamstrings'],
  adductors: ['adductors'],
  quadriceps: ['quadriceps'],
  biceps: ['biceps'],
  chest: ['chest'],
  'middle back': ['rhomboids', 'traps_middle_lower'],
  calves: ['calves'],
  glutes: ['glutes'],
  'lower back': ['lower_back'],
  lats: ['lats'],
  triceps: ['triceps'],
  traps: ['traps_upper'],
  forearms: ['forearm_flexors'],
};

const EQUIPMENT = {
  'body only': 'bodyweight',
  machine: 'machine',
  other: 'other',
  'foam roll': 'foam_roller',
  kettlebells: 'kettlebell',
  dumbbell: 'dumbbell',
  cable: 'cable',
  barbell: 'barbell',
  bands: 'bands',
  'medicine ball': 'medicine_ball',
  'exercise ball': 'stability_ball',
  'e-z curl bar': 'ez_bar',
};

const DIFFICULTY = { beginner: 2, intermediate: 3, expert: 4 };

/** Deltoid head for "shoulders", decided from the exercise name; undefined when unclear. */
function deltoidHead(name) {
  const n = name.toLowerCase();
  if (/rear|reverse fl|face pull|bent.over.*(raise|lateral)|pull apart/.test(n)) return 'deltoid_posterior';
  if (/lateral|side lateral|upright/.test(n)) return 'deltoid_lateral';
  if (/press|front|raise|push|jerk|dip/.test(n)) return 'deltoid_anterior';
  return undefined;
}

/** Maps a list of source muscles; returns slugs and review notes. */
export function mapMuscles(sourceMuscles, exerciseName) {
  const slugs = new Set();
  const notes = [];
  for (const muscle of sourceMuscles) {
    if (muscle === 'shoulders') {
      const head = deltoidHead(exerciseName);
      if (head) slugs.add(head);
      else notes.push('shoulders: deltoid head not clear from the name; chose deltoid_anterior');
      if (!head) slugs.add('deltoid_anterior');
    } else if (muscle === 'abductors') {
      slugs.add('glutes');
      notes.push('abductors mapped to glutes (gluteus medius); no abductor group exists');
    } else if (muscle === 'neck') {
      notes.push('neck dropped: no neck group in the catalog');
    } else if (DIRECT_MUSCLES[muscle]) {
      for (const slug of DIRECT_MUSCLES[muscle]) slugs.add(slug);
    } else {
      notes.push(`unknown source muscle "${muscle}"`);
    }
  }
  return { slugs: [...slugs], notes };
}

export function mapEquipment(sourceEquipment) {
  return sourceEquipment ? [EQUIPMENT[sourceEquipment] ?? 'other'] : ['bodyweight'];
}

export function mapDifficulty(level) {
  return DIFFICULTY[level] ?? 3;
}

/** Default metric from the source category and equipment; override per exercise when needed. */
export function defaultMetric(source) {
  if (source.category === 'plyometrics') return 'plyo';
  if (source.category === 'stretching' || source.category === 'cardio') return 'duration';
  if (source.equipment === 'body only' || source.equipment === null) return 'bodyweight_reps';
  return 'weight_reps';
}
