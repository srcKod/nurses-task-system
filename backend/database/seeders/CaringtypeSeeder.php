<?php

namespace Database\Seeders;

use App\Models\Caringtype;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class CaringtypeSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        DB::statement('SET FOREIGN_KEY_CHECKS = 0');
        Caringtype::Truncate();
        DB::statement('SET FOREIGN_KEY_CHECKS = 1');

        Caringtype::create([
            'name'=>'Medication',
            'description'=>'Give patient medication'
        ]);
        Caringtype::create([
            'name'=>'Changing wound',
            'description'=>'Change patient wound'
        ]);
        Caringtype::create([
            'name'=>'Catheter',
            'description'=>'Check patient catheter'
        ]);
        Caringtype::create([
            'name'=>'Injection',
            'description'=>'Give patient an injection'
        ]);
        Caringtype::create([
            'name'=>'measuring pressure',
            'description'=>'Measure patient pressure'
        ]);
        Caringtype::create([
            'name'=>'measuring heart rate',
            'description'=>'Measure patient heart rate'
        ]);
    }
}
