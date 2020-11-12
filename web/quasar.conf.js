// Configuration for your app

function extendTypescriptToWebpack(cfg) {
  // added the type-script supports
  cfg.resolve.extensions.push('.ts')
  cfg.module.rules.push({
    test: /\.ts$/,
    loader: 'ts-loader',
    options: { appendTsSuffixTo: [/\.vue$/] }
  })
}

function stripMoment(cfg) {
  const webpack = require('webpack');
  cfg.plugins.push(new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/));
}

function addMarkdownPage(cfg) {
  cfg.module.rules.push(
    {
      test: /\.md$/,
      loader: 'raw-loader'
    });
}

function add_linter(cfg) {
  cfg.module.rules.push({
    enforce: 'pre',
    test: /\.(js|vue)$/,
    loader: 'eslint-loader',
    exclude: /(node_modules|quasar)/
  });
}

function definedChunkFileName(cfg) {
  if (cfg.output)
    cfg.output.chunkFilename = 'js/[name].[id].[chunkhash:8].js';
}

module.exports = function (ctx) {
  return {
    // app plugins (/src/plugins)
    boot: [],
    css: [
      'app.styl'
    ],
    extras: [
      'roboto-font',
      'material-icons',
      //'ionicons',
      // 'mdi',
      'fontawesome-v5'
    ],
    supportTS: {
      tsCheckerConfig: {
        eslint: true
      }
    },
    supportIE: false,
    build: {
      scopeHoisting: true,
      vueRouterMode: 'history',
      // analyze: true,
      // extractCSS: false,
      // useNotifier: false,
      extendWebpack(cfg) {
        addMarkdownPage(cfg);
        // add_linter(cfg);
        definedChunkFileName(cfg);
        stripMoment(cfg);
        //extendTypescriptToWebpack(cfg);
      }
    },
    devServer: {
      // https: true,
      port: 9999,
      open: true, // opens browser window automatically
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/eventbus/*': {
          target: 'ws://localhost:8080',
          changeOrigin: true,
          ws: true
        }
      }
    },
    // framework: 'all' --- includes everything; for dev only!
    framework: {
      lang: 'fr',
      components: [
        'QLayout',
        'QHeader',
        'QDrawer',
        'QPageContainer',
        'QPage',
        'QToolbar',
        'QToolbarTitle',
        'QBtn',
        'QBtnGroup',
        'QIcon',
        'QList',
        'QItemLabel',
        'QItem',
        'QItemSection',
        'QSeparator',
        'QTabs',
        'QRouteTab',
        'QCard',
        'QCardSection',
        'QCarousel',
        'QExpansionItem',
        'QPageSticky',
        'QCardActions',
        'QTooltip',
        'QInput',
        'QDialog',
        'QLayout',
        'QInnerLoading',
        'QSpinnerGears',
        'QSpinnerDots',
        'QInfiniteScroll',
        'QTable',
        'QTh',
        'QTr',
        'QTd',
        'QToggle',
        'QSelect',
        'QField',
        'QChip',
        'QRadio',
        'QBreadcrumbs',
        'QBreadcrumbsEl',
        'QTree'
      ],
      directives: [
        'Ripple'
      ],
      // Quasar plugins
      plugins: [
        'Notify'
      ]
    },
    // animations: 'all' --- includes all animations
    animations: 'all',
    pwa: {
      cacheExt: 'js,html,css,ttf,eot,otf,woff,woff2,json,svg,gif,jpg,jpeg,png,wav,ogg,webm,flac,aac,mp4,mp3',
      manifest: {
        // name: 'Quasar App',
        // short_name: 'Quasar-PWA',
        // description: 'Best PWA App in town!',
        display: 'standalone',
        orientation: 'portrait',
        background_color: '#ffffff',
        theme_color: '#027be3',
        icons: [
          {
            'src': 'statics/icons/icon-128x128.png',
            'sizes': '128x128',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/icon-192x192.png',
            'sizes': '192x192',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/icon-256x256.png',
            'sizes': '256x256',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/icon-384x384.png',
            'sizes': '384x384',
            'type': 'image/png'
          },
          {
            'src': 'statics/icons/icon-512x512.png',
            'sizes': '512x512',
            'type': 'image/png'
          }
        ]
      }
    },
    cordova: {
      // id: 'org.cordova.quasar.app'
    },
    electron: {
      extendWebpack(cfg) {
        // do something with cfg

        console.log(cfg)
      },
      packager: {
        // OS X / Mac App Store
        // appBundleId: '',
        // appCategoryType: '',
        // osxSign: '',
        // protocol: 'myapp://path',

        // Window only
        // win32metadata: { ... }
      }
    },

    // leave this here for Quasar CLI
    starterKit: '1.0.2'
  }
};
