<?php

namespace App\Traits;

use Illuminate\Http\Request;

trait CaringTypeTrait
{
    public function saveRequestData(Request $request, $caringtype): void
    {
        $caringtype->name = ($request->name != null)?$request->name:$caringtype->name;
        $caringtype->description = $request->description;
        $caringtype->save();
    }
}
