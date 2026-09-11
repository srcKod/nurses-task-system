<?php

namespace App\Exports;

use App\Models\Caring;
use Maatwebsite\Excel\Concerns\Exportable;
use Maatwebsite\Excel\Concerns\FromQuery;

class CaringsExport implements FromQuery
{
    use Exportable;

    protected $id;
    public function __construct(int $id)
    {
        $this->id = $id;
    }

    public function query()
    {
//        dd(Caring::query()->where('nurse_id', auth()->user()?->id));
        return Caring::query()->where('nurse_id', $this->id);
    }
}
