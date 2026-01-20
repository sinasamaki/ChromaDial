// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

// https://astro.build/config
export default defineConfig({
    site: 'https://chromadial.sinasamaki.com',
	integrations: [
		starlight({
			title: 'ChromaDial',
			customCss: ['./src/styles/custom.css'],
			components: {
				ThemeSelect: './src/components/ThemeSelect.astro',
				Hero: './src/components/Hero.astro',
			},
			social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/sinasamaki/ChromaDial' }],
			sidebar: [
				{
					label: 'Getting Started',
					items: [
						{ label: 'Introduction', slug: 'guides/introduction' },
						{ label: 'Installation', slug: 'guides/installation' },
					],
				},
				{
					label: 'Components',
					items: [
						{ label: 'Dial Basics', slug: 'components/dial-basics' },
						{ label: 'Customization', slug: 'components/customization' },
					],
				},
				{
					label: 'API Reference',
					items: [
						{ label: 'DialState', slug: 'reference/dial-state' },
						{ label: 'RadiusMode', slug: 'reference/radius-mode' },
					],
				},
			],
		}),
	],
});
