// Shared body for every Phase 1 template: title, paragraphs, optional code, steps and note.
import { Heading, Text } from '@react-email/components';
import { CodeBox } from './components/code-box.js';
import { MailLayout } from './components/mail-layout.js';
import { styles } from './components/mail-styles.js';

export interface StandardMailProps {
  readonly lang: string;
  readonly appName: string;
  readonly preheader: string;
  readonly title: string;
  readonly paragraphs: readonly string[];
  readonly code?: string | undefined;
  readonly steps?: readonly string[] | undefined;
  readonly note?: string | undefined;
  readonly footerLines: readonly string[];
}

export function StandardMail(props: StandardMailProps) {
  return (
    <MailLayout
      lang={props.lang}
      preheader={props.preheader}
      appName={props.appName}
      footerLines={props.footerLines}
    >
      <Heading as="h1" style={styles.title}>
        {props.title}
      </Heading>
      {props.paragraphs.map((paragraph) => (
        <Text key={paragraph} style={styles.paragraph}>
          {paragraph}
        </Text>
      ))}
      {props.code === undefined ? null : <CodeBox code={props.code} />}
      {props.steps?.map((step, index) => (
        <Text key={step} style={styles.step}>
          {`${String(index + 1)}. ${step}`}
        </Text>
      ))}
      {props.note === undefined ? null : <Text style={styles.note}>{props.note}</Text>}
    </MailLayout>
  );
}
