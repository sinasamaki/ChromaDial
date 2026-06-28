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
						{ label: 'Default Dial Colors', slug: 'components/dial-colors' },
						{ label: 'Custom Thumb & Track', slug: 'components/customization' },
						{ label: 'State-based API', slug: 'components/state-based-api' },
						{ label: 'Common Patterns', slug: 'components/common-patterns' },
						{ label: 'Overshoot', slug: 'components/overshoot' },
						{ label: 'Responding to Input', slug: 'components/responding-to-input' },
					],
				},
				{
					label: 'API Reference',
					items: [
						{ label: 'DialState', slug: 'reference/dial-state' },
						{ label: 'DialColors', slug: 'reference/dial-colors' },
						{ label: 'DialLayout', slug: 'reference/dial-layout' },
						{ label: 'RadiusMode', slug: 'reference/radius-mode' },
					],
				},
			],
		}),
	],
});
