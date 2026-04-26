import fs from 'node:fs/promises';
import path from 'node:path';

const ROOT = process.cwd();
const FRANKDOC_PATH = path.join(ROOT, 'src/js/frankdoc.json');
const SITEMAP_PATH = path.join(ROOT, 'src/sitemap.xml');
const BASE_URL = process.env.SITEMAP_BASE_URL ?? 'https://frankdoc.frankframework.org';

const staticRoutes = [
  '/',
  '/#/search',
  '/#/components',
  '/#/properties',
  '/#/credential-providers',
  '/#/servlet-authenticators',
];

const escapeCharacters = (character) =>
  character
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&apos;');

const buildSitemapUrl = (loc, priority, changefreq) => `  <url>
    <loc>${escapeCharacters(loc)}</loc>
    <changefreq>${changefreq}</changefreq>
    <priority>${priority}</priority>
  </url>`;

const withBase = (route) => `${BASE_URL}${route}`;

const build = async () => {
  const frankdocFile = await fs.readFile(FRANKDOC_PATH, 'utf8');
  const ffdoc = JSON.parse(frankdocFile);

  const urls = new Set(staticRoutes.map((element) => withBase(element)));

  if (ffdoc?.elements) {
    for (const elementName of Object.keys(ffdoc.elements)) {
      if (elementName.includes('.')) urls.add(withBase(`/#/${encodeURIComponent(elementName)}`));
    }
  }

  if (ffdoc?.credentialProviders) {
    for (const name of Object.keys(ffdoc.credentialProviders)) {
      urls.add(withBase(`/#/credential-providers/${encodeURIComponent(name)}`));
    }
  }

  if (ffdoc?.servletAuthenticators) {
    for (const name of Object.keys(ffdoc.servletAuthenticators)) {
      urls.add(withBase(`/#/servlet-authenticators/${encodeURIComponent(name)}`));
    }
  }

  const body = [...urls]
    .map((url) => buildSitemapUrl(url, '1.0', 'weekly'))
    .join('\n  ')
    .trim();

  const xml = `<?xml version="1.0" encoding="UTF-8"?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  ${body}
</urlset>
 `;

  await fs.writeFile(SITEMAP_PATH, xml, 'utf8');
  console.log(`Generated sitemap: ${SITEMAP_PATH}`);
};

try {
  await build();
} catch (error) {
  console.error('Error generating sitemap:', error);
}
