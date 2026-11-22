// Large one-time code with tabular digits.
import { Section, Text } from '@react-email/components';
import { styles } from './mail-styles.js';

export function CodeBox({ code }: { readonly code: string }) {
  return (
    <Section style={styles.codeBox}>
      <Text style={styles.code}>{code}</Text>
    </Section>
  );
}
