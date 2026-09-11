<?php

namespace Database\Factories;

use App\Models\Caringtype;
use App\Models\Nurse;
use App\Models\Patient;
use Illuminate\Database\Eloquent\Factories\Factory;

/**
 * @extends \Illuminate\Database\Eloquent\Factories\Factory<\App\Models\Caring>
 */
class CaringFactory extends Factory
{
    /**
     * Define the model's default state.
     *
     * @return array<string, mixed>
     */
    public function definition(): array
    {
        return [
            'time'=>fake()->dateTime(),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>fake()->text(300),
            'is_finished'=>fake()->boolean(25)
        ];
    }
}
