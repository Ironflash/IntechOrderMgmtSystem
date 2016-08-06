/***********************************************************************************************
 * User Configuration.
 **********************************************************************************************/
/** Map relative paths to URLs. */
const map: any = {
  'ng2-file-upload':'vendor/ng2-file-upload',
  'ng2-bootstrap': 'vendor/ng2-bootstrap',
  'ng2-pdf-viewer': 'vendor/ng2-pdf-viewer',
  'pdfjs-dist': 'vendor/pdfjs-dist'
};

/** User packages configuration. */
const packages: any = {
  'ng2-file-upload':{
    format: 'cjs'
  },
  'ng2-bootstrap': {
    format: 'cjs',
    defaultExtension: 'js',
    main: 'ng2-bootstrap.js'
  },
  'ng2-pdf-viewer': {
    format: 'cjs',
    main: 'dist/pdf-viewer.component.min.js'
  },
  'pdfjs-dist': {
    format: 'cjs'
  }
};

////////////////////////////////////////////////////////////////////////////////////////////////
/***********************************************************************************************
 * Everything underneath this line is managed by the CLI.
 **********************************************************************************************/
const barrels: string[] = [
  // Angular specific barrels.
  '@angular/core',
  '@angular/common',
  '@angular/compiler',
  '@angular/http',
  '@angular/router',
  '@angular/platform-browser',
  '@angular/platform-browser-dynamic',

  // Thirdparty barrels.
  'rxjs',

  // App specific barrels.
  'app',
  'app/shared',
  'app/purchase-order',
  'app/purchase-order/upload',
  'app/purchase-order/purchas-order-upload',
  'app/purchase-order/purchase-order-upload',
  'app/purchase-order/purchase-order-upload/file-input',
  /** @cli-barrel */
];

const cliSystemConfigPackages: any = {};
barrels.forEach((barrelName: string) => {
  cliSystemConfigPackages[barrelName] = { main: 'index' };
});

/** Type declaration for ambient System. */
declare var System: any;

// Apply the CLI SystemJS configuration.
System.config({
  map: {
    '@angular': 'vendor/@angular',
    'rxjs': 'vendor/rxjs',
    'main': 'main.js'
  },
  packages: cliSystemConfigPackages
});

// Apply the user's configuration.
System.config({ map, packages });
