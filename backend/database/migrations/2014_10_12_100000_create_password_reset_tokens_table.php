<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        $connection = Schema::getConnection();

        // MySQL/MariaDB refuse `create table` without a primary key when
        // sql_require_primary_key is enabled (Wasmer's managed MySQL sets it).
        // Laravel 10.7's MySQL grammar adds the primary key as a *separate*
        // `alter table` statement, so the `create table` itself is rejected
        // first (error 3750). Emit the DDL with an inline primary key instead.
        // See BTL13 in ../../../KNOWN-ISSUES.md.
        if ($connection->getDriverName() === 'mysql') {
            $charset = preg_replace('/[^A-Za-z0-9_]/', '', (string) $connection->getConfig('charset')) ?: 'utf8mb4';
            $collation = preg_replace('/[^A-Za-z0-9_]/', '', (string) $connection->getConfig('collation')) ?: 'utf8mb4_unicode_ci';

            $connection->statement(
                'create table `password_reset_tokens` ('
                .'`email` varchar(255) not null, '
                .'`token` varchar(255) not null, '
                .'`created_at` timestamp null, '
                .'primary key (`email`)'
                .") default character set {$charset} collate '{$collation}'"
            );

            return;
        }

        Schema::create('password_reset_tokens', function (Blueprint $table) {
            $table->string('email');
            $table->string('token');
            $table->timestamp('created_at')->nullable();
            $table->primary('email');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('password_reset_tokens');
    }
};
