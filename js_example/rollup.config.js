import nodeResolve from '@rollup/plugin-node-resolve'
import commonjs from '@rollup/plugin-commonjs'
import json from '@rollup/plugin-json';
import terser from '@rollup/plugin-terser';
// import externals from "rollup-plugin-node-externals"
import nodePolyfills from 'rollup-plugin-polyfill-node'
import postcss from "rollup-plugin-postcss"
import typescript from "@rollup/plugin-typescript"
import strip from "@rollup/plugin-strip";
import babel from '@rollup/plugin-babel';
export default {
  input: 'src/index.js',
  output: [
    {
      file: 'lib/bundle.js',
      format: 'iife',
      name: 'openApiRequest',
      plugins: [terser()] //压缩iife
    },
    {
      file: 'lib/bundle-umd.js',
      format: 'umd',
      name: 'openApiRequest'
    },
    {
      file: 'lib/bundle-es.js',
      format: 'es',
      // dir: 'dist',
      // exports: 'named', // 指定导出模式（自动、默认、命名、无）
      // preserveModules: true, // 保留模块结构
      // preserveModulesRoot: 'src', // 将保留的模块放在根级别的此路径下
    },
    {
      file: 'lib/bundle-cjs.js',
      format: 'cjs'
    }
  ],
  external: [
      'react',
      'vue'
  ],
  onwarn: function (warning) {
    if (warning.code === 'THIS_IS_UNDEFINED') {
        return;
    }
    console.error(warning.message);
  },
  plugins: [
    json(),
    nodeResolve(),
    commonjs(),
    postcss(),
    nodePolyfills(/* options */),
    // externals({
    //   devDeps: false, // devDependencies 类型的依赖就不用加到 externals 了。
    // }),
    typescript(),
    // typescript({
    //     outDir: "dist",
    //     declaration: true,
    //     declarationDir: "dist",
    // }),
    // strip()
    babel({
      babelHelpers: 'bundled',
      exclude: 'node_modules/**'
    })
  ]
}
