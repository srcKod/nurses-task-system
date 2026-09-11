<?php

namespace Database\Seeders;

use App\Models\Caring;
use App\Models\Caringtype;
use App\Models\Nurse;
use App\Models\Patient;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class CaringSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::statement('SET FOREIGN_KEY_CHECKS = 0');
        Caring::Truncate();
        DB::statement('SET FOREIGN_KEY_CHECKS = 1');

        Caring::create([
            'time'=>now('Asia/Damascus')->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(1)->addMinutes(9)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(2)->addMinutes(5)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(3)->addMinutes(24)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(4)->addMinutes(13)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(5)->addMinutes(15)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(1)->addMinutes(16)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(1)->addMinutes(20)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(1)->addMinutes(48)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
        Caring::create([
            'time'=>now('Asia/Damascus')->addHours(1)->addMinutes(32)->format('g:i A'),
            'nurse_id'=>Nurse::inRandomOrder()->first()->id,
            'caringtype_id'=>Caringtype::inRandomOrder()->first()->id,
            'patient_id'=>Patient::inRandomOrder()->first()->id,
            'description'=>null,
            'is_finished'=>0
        ]);
    }
}
