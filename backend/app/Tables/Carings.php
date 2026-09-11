<?php

namespace App\Tables;

use App\Models\Caring;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use PhpOffice\PhpSpreadsheet\Style\NumberFormat;
use ProtoneMedia\Splade\AbstractTable;
use ProtoneMedia\Splade\Facades\Toast;
use ProtoneMedia\Splade\SpladeTable;
use ProtoneMedia\Splade\Table\LaravelExcelException;

class Carings extends AbstractTable
{
    protected $query;
    /**
     * Create a new instance.
     *
     * @return void
     */
    public function __construct()
    {
        //
    }

    /**
     * Determine if the user is authorized to perform bulk actions and exports.
     *
     * @param Request $request
     * @return bool
     */
    public function authorize(Request $request): bool
    {
        return true;
    }

    /**
     * The resource or query builder.
     *
     * @return Builder
     */
    public function for(): Builder
    {
        $query=Caring::query();
        if(Route::currentRouteName()=="carings.index"){
            $query=Caring::query();
        }
        elseif(Route::currentRouteName()=="dashboard"){
            $query= Caring::query()->where('nurse_id', auth()->user()?->getAttributeValue('id'));
        }
        return $query;
    }

    /**
     * Configure the given SpladeTable.
     *
     * @param SpladeTable $table
     * @return void
     * @throws LaravelExcelException
     */
    public function configure(SpladeTable $table): void
    {
          $table
            ->withGlobalSearch(columns: ['id','time','nurse_id'])
            ->column('id', sortable: true)
            ->column('nurse_id','Nurse',sortable: true,
                exportAs: fn ($nurse_id) => Caring::where('nurse_id',$nurse_id)->first()->nurse->name)
            ->column('caringtype_id','Caring Type',sortable: true,
                exportAs: fn ($caringtype_id) => Caring::where('caringtype_id',$caringtype_id)->first()->caringtype->name)
            ->column('patient_id','Patient',sortable: true,
                exportAs: fn ($patient_id) => Caring::where('patient_id',$patient_id)->first()->patient->name)
            ->column('time','Time',sortable: true, searchable: true,
                exportFormat: NumberFormat::FORMAT_DATE_YYYYMMDD)
            ->column('description','Description',searchable: true)
            ->column('is_finished',
                'Status',exportAs: fn ($is_finished) => ($is_finished)?"Finished":"Pending")
            ->column('action')
            ->export()
            ->paginate(15);
            // ->searchInput()
            // ->selectFilter()
            // ->withGlobalSearch()

            // ->bulkAction()
            // ->export()
    }
}
