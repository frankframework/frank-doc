module.exports = {
  root: true,
  ignorePatterns: ['projects/**/*'],
  overrides: [
    {
      files: ['*.ts'],
      parserOptions: {
        project: ['tsconfig.app.json', 'tsconfig.spec.json'],
        tsconfigRootDir: __dirname,
        createDefaultProgram: true,
        sourceType: 'module',
      },
      extends: [
        'plugin:@angular-eslint/recommended',
        'plugin:@angular-eslint/template/process-inline-templates',
        'plugin:unicorn/recommended',
      ],
      rules: {
        'unicorn/prevent-abbreviations': 'warn',
        'unicorn/empty-brace-spaces': 'off',
        'unicorn/no-array-reduce': 'off',
        'unicorn/prefer-ternary': 'warn',
        'unicorn/no-null': 'warn',
        '@typescript-eslint/explicit-function-return-type': 'error',
      },
    },
    {
      files: ['*.html'],
      extends: ['plugin:@angular-eslint/template/recommended'],
      rules: {},
    },
  ],
};
