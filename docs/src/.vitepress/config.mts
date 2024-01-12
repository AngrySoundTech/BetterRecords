import { defineConfig } from 'vitepress'
import { withMermaid} from "vitepress-plugin-mermaid";

// https://vitepress.dev/reference/site-config
export default withMermaid(defineConfig({
  title: "BetterRecords Docs",
  description: "BetterRecords Docs",
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: 'Home', link: '/' },
    ],

    sidebar: [
      {
        text: 'Get Started',
        link: 'pages/get-started',
      },
      {
        text: 'Technical Documentation',
        items: [
          { text: 'Downloading Sequence', link: 'pages/technical/downloading-sequence' }
        ],
      },
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/AngrySoundTech/BetterRecords' }
    ]
  },
}))
