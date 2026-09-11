<?php

namespace App\Exports;

use App\Models\CaringType;
use Maatwebsite\Excel\Concerns\FromCollection;

class CaringTypesExport implements FromCollection
{
    /**
    * @return \Illuminate\Support\Collection
    */
    public function collection()
    {
        return CaringType::all();
    }
}
