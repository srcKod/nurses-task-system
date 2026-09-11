<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Carings') }}
            </h2>

{{--            <Link modal href="{{route('carings.create')}}"--}}
{{--                  class="px-4 py-2 bg-indigo-500 hover:bg-indigo-100 text-white rounded-md"> New Caring </Link>--}}
{{--            <Link href="{{route('carings.trashed')}}"--}}
{{--                  class="px-4 py-2 bg-indigo-500 hover:bg-indigo-300 text-white rounded-md"> Show Trashed </Link>--}}
        </div>
    </x-slot>
    <div class="py-12">
        <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
            <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                <div class="p-7 text-gray-900">
                    <x-splade-table :for="$carings">
                        <x-splade-cell nurse_id>
                            {{ $item->nurse->name }}
                        </x-splade-cell>
                        <x-splade-cell caringtype_id>
                            {{ $item->caringtype->name }}
                        </x-splade-cell>
                        <x-splade-cell patient_id>
                            {{ $item->patient->name }}
                        </x-splade-cell>
                        <x-splade-cell is_finished>
                            @if($item->is_finished)
                                {{ ('Finished') }}
                            @else
                                {{ ('Pending') }}
                            @endif
                        </x-splade-cell>
                        <x-splade-cell time>
                            {{ $item->time->format('Y-m-d g:i A') }}
                        </x-splade-cell>
                        <x-splade-cell action>
                            <Link modal href="{{route('carings.edit',$item)}}" class="font-bold text-indigo-600"> Edit </Link>
{{--                            &nbsp--}}
{{--                            <Link href="{{route('carings.destroy',$item)}}" method="DELETE"--}}
{{--                                  class="font-bold text-indigo-600"--}}
{{--                                  confirm="Delete" confirm-text="Are you sure you want to delete">--}}
{{--                                Delete--}}
{{--                            </Link>--}}
                            &nbsp
                            <Link method="PATCH" href="{{route('carings.check',$item->id)}}" class="font-bold text-indigo-600"> Check </Link>
                            &nbsp
                            @if($item->deleted_at)
                              <Link method="PATCH" href="{{route('carings.restore',$item->id)}}" class="font-bold text-indigo-600"> Restore </Link>
                            @endif
                        </x-splade-cell>
                    </x-splade-table>
                </div>
            </div>
        </div>
    </div>
</x-app-layout>
