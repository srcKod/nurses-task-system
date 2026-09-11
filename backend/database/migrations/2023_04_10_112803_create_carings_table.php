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
        Schema::create('carings', function (Blueprint $table) {
            $table->id();
            $table->foreignId('nurse_id')->constrained()->cascadeOnDelete();
            $table->foreignId('caringtype_id')->constrained()->cascadeOnDelete();
            $table->foreignId('patient_id')->constrained()->cascadeOnDelete();
            $table->timestamp('time')->unique();
            $table->string('description',300)->nullable();
            $table->boolean('is_finished')->default(0);
            $table->timestamps();
            $table->softDeletes();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('carings');
    }
};
