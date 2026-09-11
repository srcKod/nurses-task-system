<?php

namespace App\Exports;

use App\Models\Nurse;
use Maatwebsite\Excel\Concerns\FromCollection;

class NursesExport implements FromCollection
{
    /**
    * @return \Illuminate\Support\Collection
    */
    public function collection()
    {
        return Nurse::all();
    }
}
