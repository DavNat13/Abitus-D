// Common structure: preheader, brand header, content and footer (15b-CORREOS-PLANTILLAS.md).
import { Body, Container, Head, Hr, Html, Preview, Text } from '@react-email/components';
import type { ReactNode } from 'react';
import { styles } from './mail-styles.js';

interface MailLayoutProps {
  readonly lang: string;
  readonly preheader: string;
  readonly appName: string;
  readonly footerLines: readonly string[];
  readonly children: ReactNode;
}

export function MailLayout({ lang, preheader, appName, footerLines, children }: MailLayoutProps) {
  return (
    <Html lang={lang}>
      <Head>
        <meta name="color-scheme" content="light" />
      </Head>
      <Preview>{preheader}</Preview>
      <Body style={styles.body}>
        <Container style={styles.container}>
          <Text style={styles.brand}>{appName}</Text>
          {children}
          <Hr style={styles.divider} />
          {footerLines.map((line) => (
            <Text key={line} style={styles.footer}>
              {line}
            </Text>
          ))}
        </Container>
      </Body>
    </Html>
  );
}
