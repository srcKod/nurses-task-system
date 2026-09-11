<?php

return [

    /*
    |--------------------------------------------------------------------------
    | Title and meta tags (SEO)
    |--------------------------------------------------------------------------
    |
    | You may use the SEO facade to set your page's title, description, and keywords.
    | @see https://splade.dev/docs/title-meta
    |
    */

    'defaults' => [
        'title'       => env('APP_NAME', 'E-Nurse Caring'),
        'description' => 'E-Nurse Management System',
        'keywords'    => ['E-Nurse', 'Caring'],
    ],

    'title_prefix'    => '',
    'title_separator' => '|',
    'title_suffix'    => 'E-Nurse',

    'auto_canonical_link' => true,

    'open_graph' => [
        'auto_fill' => false,
        'image'     => "https://example.com/public/files/logo.png",
        'site_name' => "E-Nurse",
        'title'     => "E-Nurse Caring",
        'type'      => 'WebPage', // 'WebPage'
        'url'       => "https://enurse.example.com/",
    ],

    'twitter' => [
        'auto_fill'   => false,
        'card'        => "https://example.com/public/files/logo.png", // 'summary_large_image',
        'description' => 'E-Nurse Management System',
        'image'       => "https://example.com/public/files/logo.png",
        'site'        => null, // '@username',
        'title'       => "E-Nurse Caring",
    ],

];
